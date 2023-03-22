(ns my-jackdaw.client.subscribed-consumer
  (:require
    [jackdaw.client :as jc]))

(def consumer-config
  {"bootstrap.servers" "localhost:9092"
   "group.id" "com.foo.my-consumer"
   "key.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"
   "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"})

(def topic-config
  {:topic-name "jackdaw"})

(defn poll-and-loop!
  "Continuously fetches records every `poll-ms`, processes them with `processing-fn` and commits offset after each poll."
  [consumer processing-fn continue?]
  (let [poll-ms 5000]
    (loop []
      (if @continue?
        (let [records (jc/poll consumer poll-ms)]
          (when (seq records)
            (processing-fn records)
            (.commitSync consumer))
          (recur))))))

(defn process-messages! [topic-config processing-fn]
  (let [continue? (atom true)]
    (with-open [consumer (jc/subscribed-consumer consumer-config [topic-config])]
      (poll-and-loop! consumer processing-fn continue?))))

(defn process-message [consumer-name m]
  (println consumer-name m                                  ;:key (:key m) :value (:value m)
           ))

(defn run [consumer-name]
  (process-messages! topic-config (fn [ms]
                                    (println "processing messages")
                                    (doseq [m ms]
                                      (process-message consumer-name m))
                                    (println "done processing messages")
                                    )))

(def my-consumer1 (future (run "consumer 1")))
(def my-consumer2 (future (run "consumer 2")))
(def my-consumer3 (future (run "consumer 3")))
(def my-consumer4 (future (run "consumer 4")))
(def my-consumer5 (future (run "consumer 5")))

(future-cancel my-consumer1)
(future-cancel my-consumer2)
(future-cancel my-consumer3)
(future-cancel my-consumer4)
(future-cancel my-consumer5)

(jc/partitions-for my-consumer1 {:topic-name "jackdaw"})

