(ns nbt-clj.codecs
  (:use clojure.java.io
        gloss.core
        [gloss.data.bytes.core :exclude (byte-count)]
        [gloss.core.protocols :exclude (sizeof)]
        gloss.io))


;This is a first pass at a gloss codec to handle the NBT TAG_Compound structure
;It can only handle decoding (reading) at the moment, and only a single byte delimiter
;
;Future plans include having it terminate on an arbitrary delimiter length
;and to support encoding (writing)
(defn terminated-repeat [delimiter-byte codec]
  "A gloss codec that repeats a sub-codec until a terminating byte is read.
   This only handles a terminating byte, and can only perform reads at the moment"
  ; try to read the delimiter
  ; if success
  ;   strip delimiter
  ;   return [true vals remaining] ; this is [success, decoded data, remaining byte buffers]
  ; elseif not success
  ;   [s,v,r] = read codec
  ;   if not s
  ;     return [false 'this' buf-seq] ; this is [failure, a continuation, byte buffers]
  ;   elseif success
  ;     vals append v
  ;     recur vals r(emaining)

  (reify
    Reader
    (read-bytes [_ buf-seq]
      (let [byte-codec (compile-frame :byte)]
        (loop [results [] 
               bufs buf-seq]
          (let [[found x bytes] (read-bytes byte-codec (take-bytes
                                                         (dup-bytes bufs)
                                                         1))]
            (if (or
                  (and found (== x delimiter-byte))
                  (== 0 (byte-count bufs)))
              [true results (drop-bytes bufs 1)]
              (let [[success x b] (read-bytes codec bufs)]
                (if success
                  (recur (conj results x) b)
                  [false nil nil])))))))  ; This is incorrect right now
                                          ; After the false, it needs to return a continuation and the remaining byte-buffers
                                          ; Doing that would allow stream processing 
    
    
    Writer
    (sizeof[_]
      nil)
    (write-bytes [_ buf val]
      (throw (Exception. "write-bytes not supported")))))


