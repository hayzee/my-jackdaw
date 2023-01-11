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

(def outcome (jc/produce! my-producer {:topic-name "jackdaw"} "1" "hi mom!"))

(type outcome)
; clojure.lang.Delay

(realized? outcome)

(deref outcome)
; Result of sending is a map
; {:topic-name "jackdaw",
;  :partition 9,
;  :timestamp 1673391975850,
;  :offset 0,
;  :serialized-key-size 1,
;  :serialized-value-size 7}

; Note can (and probably should) combine jc/producer and jc/produce!
(with-open [my-producer (jc/producer producer-config)]
  (dotimes [n 100]
    @(jc/produce! my-producer {:topic-name "jackdaw"} (str n) "Yo mom!")))
