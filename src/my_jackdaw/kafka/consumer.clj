(ns my-jackdaw.kafka.consumer
  (:require [jackdaw.client :as jc]
            [taoensso.timbre :refer [info]]))

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

(defn- process-messages!
  [consumer-name consumer-config topic-config processing-fn continue?]
  (info "Consumer " consumer-name " - stopped")
  (with-open [consumer (jc/subscribed-consumer consumer-config [topic-config])]
    (poll-and-loop! consumer processing-fn continue?))
  (info "Consumer " consumer-name " - stopped"))

(defn get-consumer
  [consumer-name]
  (get @consumers consumer-name))

(defn create-consumer
  [consumer-name consumer-config topic-config processing-fn]
  (if (get-consumer consumer-name)
    (throw (ex-info "Consumer already exists" {:consumer-name consumer-name}))
    (let [continue? (atom true)]
      (swap! consumers
             assoc
             consumer-name {:stopper-fn (fn [] (swap! continue? not))
                            :process    (future (process-messages! consumer-name consumer-config topic-config processing-fn continue?))}))))

(defn stop-consumer
  [consumer-name]
  (if-let [consumer (get-consumer consumer-name)]
    (do ((:stopper-fn consumer))
        (swap! consumers dissoc consumer-name))
    (throw (ex-info "No such producer" {:client-id consumer-name}))))

(defn list-consumers
  []
  (keys @consumers))

(defn stop-all-consumers
  []
  (doseq [consumer-name (list-consumers)]
    (stop-consumer consumer-name)))
