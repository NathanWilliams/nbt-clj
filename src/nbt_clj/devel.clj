(ns nbt-clj.devel
  (:use nbt-clj.nbt
        [gloss.io :only [to-byte-buffer decode]]
        [clojure.java.io :only [file input-stream]]))
        ;[clojure.tools.namespace.repl :only [refresh]]))

;(ns nbt-clj.devel)
;(use '[clojure.tools.namespace.repl :only (refresh)])
;(refresh)

(def filename "nbt-samples/bigtest.nbt")
;(def filename "nbt-samples/test.nbt")


(def data (with-open [f (input-stream filename)]
            (let [size (.length (file filename))
                  ba (byte-array size)]
              (.read f ba)
              (to-byte-buffer ba))))

(def result (decode root data))
