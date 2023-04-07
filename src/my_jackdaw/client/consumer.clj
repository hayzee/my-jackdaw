(ns my-jackdaw.client.consumer
  (:require [jackdaw.client :as jc]))

(defonce ^:private consumers (atom {}))

(defn- poll-and-loop!
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

(defn- process-messages! [consumer-config topic-config processing-fn continue?]
  (with-open [consumer (jc/subscribed-consumer consumer-config [topic-config])]
    (poll-and-loop! consumer processing-fn continue?)))

(defn create-consumer [consumer-config topic-config processing-fn]
  (let [continue? (atom true)]
   {:continue? continue?
    :process (future (process-messages! consumer-config topic-config processing-fn continue?))}))

