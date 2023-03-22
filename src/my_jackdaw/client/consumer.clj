(ns my-jackdaw.client.consumer
  (:require [jackdaw.client :as jc])
  (:import (org.apache.kafka.clients.admin AdminClient)))

; CONSUMER

(def consumer-config
  {"bootstrap.servers" "localhost:9092"
   "group.id" "com.my.group"
   "key.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"
   "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"})

(def consumer (jc/subscribed-consumer consumer-config [{:topic-name "jackdaw"}]))
(def consumer2 (jc/subscribed-consumer consumer-config [{:topic-name "jackdaw"}]))
(def consumer3 (jc/subscribed-consumer consumer-config [{:topic-name "jackdaw"}]))
(def consumer4 (jc/subscribed-consumer consumer-config [{:topic-name "jackdaw"}]))
(def consumer5 (jc/subscribed-consumer consumer-config [{:topic-name "jackdaw"}]))

(def cloop1 (future
              (loop []
                (let [recs (jc/poll consumer 1000)]
                  (doseq [r recs]
                    (clojure.pprint/pprint (type r)))
                  )
                (recur))))

(future-cancel cloop1)

(jc/poll consumer2 10)
(jc/poll consumer3 10)

(jc/num-partitions consumer {:topic-name "jackdaw"})
(jc/num-partitions consumer2 {:topic-name "jackdaw"})

(jc/position-all consumer)

(map count [(jc/partitions-for consumer {:topic-name "jackdaw"})
  (jc/partitions-for consumer2 {:topic-name "jackdaw"})
  (jc/partitions-for consumer3 {:topic-name "jackdaw"})])

(apply jc/assign consumer2 (.partitionsFor consumer2 "jackdaw"))


(doseq [tp (map first (jc/end-offsets consumer (jc/partitions-for consumer {:topic-name "jackdaw"})))]
  (jc/seek consumer tp 0))

(def cons2 (jc/seek-to-beginning-eager consumer))

(jc/end-offsets consumer (jc/partitions-for cons2 {:topic-name "jackdaw"}))

(def poll-ms 5000)

(def records (jc/poll consumer2 1000))

(count records)

;(jc/consumer )

;
;
;
;(.listConsumerGroups (.AdminClient))

(jc/)