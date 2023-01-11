(ns my-jackdaw.client.consumer
  (:require [jackdaw.client :as jc]))

; CONSUMER

(def consumer-config
  {"bootstrap.servers" "localhost:9092"
   "group.id" "com.foo.my-consumer"
   "key.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"
   "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"})

(def consumer (jc/subscribed-consumer consumer-config [{:topic-name "jackdaw"}]))

(jc/partitions-for consumer {:topic-name "jackdaw"})

(def consumer2 (jc/subscribed-consumer consumer-config [{:topic-name "jackdaw"}]))

(jc/partitions-for consumer {:topic-name "jackdaw"})
(jc/partitions-for consumer2 {:topic-name "jackdaw"})

(doseq [tp (map first (jc/end-offsets consumer (jc/partitions-for consumer {:topic-name "jackdaw"})))]
  (jc/seek consumer tp 0))

(def cons2 (jc/seek-to-beginning-eager consumer))

(jc/end-offsets consumer (jc/partitions-for cons2 {:topic-name "jackdaw"}))

(def poll-ms 5000)

(def records (jc/poll consumer 1000))

(count records)
