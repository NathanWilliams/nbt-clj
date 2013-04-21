(ns nbt-clj.nbt
  (:use nbt-clj.codecs
        clojure.java.io
        gloss.core
        [gloss.data.bytes.core :exclude (byte-count)]
        [gloss.core.protocols :exclude (sizeof)]
        gloss.io))


(defcodec tag-type (enum :byte {:TAG_End        0
                                :TAG_Byte       1
                                :TAG_Short      2
                                :TAG_Int        3
                                :TAG_Long       4
                                :TAG_Float      5
                                :TAG_Double     6
                                :TAG_Byte_Array 7
                                :TAG_String     8
                                :TAG_List       9
                                :TAG_Compound   10
                                :TAG_Int_Array  11}))

(defcodec sized-string (finite-frame :uint16-be 
                                     (string :utf-8)))


(defcodec tag-byte
  (ordered-map :tag-type    :TAG_Byte
               :payload     :byte))

(defcodec tag-short      
  (ordered-map :tag-type    :TAG_Short  
               :payload     :int16-be));Minecraft uses bigendian (the correct endiness!)

(defcodec tag-int        
  (ordered-map :tag-type    :TAG_Int    
               :payload     :int32-be))

(defcodec tag-long       
  (ordered-map :tag-type    :TAG_Long   
               :payload     :int64-be))

(defcodec tag-float      
  (ordered-map :tag-type    :TAG_Float  
               :payload     :float32-be))

(defcodec tag-double     
  (ordered-map :tag-type    :TAG_Double 
               :payload     :float64-be))

(defcodec tag-byte-array 
  (ordered-map :tag-type    :TAG_Byte_Array
               :payload (repeated :byte 
                                  :prefix :int32-be)))

(defcodec tag-string 
  (ordered-map :tag-type    :TAG_String 
               :payload sized-string))

(defcodec tag-int-array  
  (ordered-map :tag-type    :TAG_Int_Array
               :payload (repeated :int32-be
                                  :prefix :int32-be)))



(declare tag-compound tag-list)


(defn get-codec [t]
  (t {:TAG_End        (compile-frame [:error :ErrorNoFnForEnd]) ; this should never be called
      :TAG_Byte       tag-byte
      :TAG_Short      tag-short
      :TAG_Int        tag-int
      :TAG_Long       tag-long
      :TAG_Float      tag-float
      :TAG_Double     tag-double
      :TAG_Byte_Array tag-byte-array
      :TAG_String     tag-string
      :TAG_List       tag-list
      :TAG_Compound   tag-compound
      :TAG_Int_Array  tag-int-array}))


(defn get-named-codec [t]
  (compile-frame
    (ordered-map  :tag-name    sized-string
                  :payload (get-codec t))
    identity ;pre-encoder
    (fn [x] (merge x (:payload x)))))

(defcodec tag-list       
  (ordered-map :tag-type  :TAG_List 
               :payload (header tag-type ;tag-type is a frame which maps a byte to an enum name
                                (fn [t] ;returns a repeated codec of the right length
                                  (compile-frame 
                                    (repeated (get-codec t) 
                                              :prefix :int32-be)))
                                :tag-type))) 


(defcodec tag-compound
  (ordered-map :tag-type    :TAG_Compound
               :payload (terminated-repeat 0x00
                          (header tag-type
                                  get-named-codec
                                  :tag-type))))

(defcodec root (header tag-type
                       get-named-codec
                       :tag-type))

(defn decode-nbt [data]
  (decode root data))
