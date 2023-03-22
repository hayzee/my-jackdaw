(ns my-jackdaw.client.producer
  (:require [jackdaw.client :as jc]))

; PRODUCER

(def producer-config
  {"bootstrap.servers" "localhost:9092"
   "key.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "value.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "acks" "all"
   "client.id" "foo"})

(def my-producer (jc/producer producer-config))

; (.close my-producer)

; (def outcome (jc/produce! my-producer {:topic-name "jackdaw"} "1" "hi mom!"))

; (type outcome)
; clojure.lang.Delay

; (realized? outcome)

; (deref outcome)
; Result of sending is a map
; {:topic-name "jackdaw",
;  :partition 9,
;  :timestamp 1673391975850,
;  :offset 0,
;  :serialized-key-size 1,
;  :serialized-value-size 7}

; Note can (and probably should) combine jc/producer and jc/produce!
;(with-open [my-producer (jc/producer producer-config)]
;  (dotimes [n 10]
;    @(jc/produce! my-producer {:topic-name "jackdaw"} (str n) "Yo mom!")))

;(take 10 (repeatedly #(rand-int 10)))


(def results
  (pmap
    #(deref (jc/produce! my-producer {:topic-name "jackdaw"} (str (rand-int %)) "Yo mom!"))
    (repeat 100000 1000)))

(map #(vector
       (first %)
       (count (second %)))
     (group-by :partition results))

(map first (group-by :partition results))
