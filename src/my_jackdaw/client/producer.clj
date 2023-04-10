(ns my-jackdaw.client.producer
  (:require [jackdaw.client :as jc])
  (:import (org.apache.kafka.clients.producer KafkaProducer)))

(defonce ^:private producers (atom {}))

(defn get-producer
  [client-id]
  (get @producers client-id))

(defn create-producer
  [client-id producer-config]
  (if (get-producer client-id)
    (throw (ex-info "Producer already exists" {"client.id" client-id}))
    (swap! producers assoc client-id (jc/producer (assoc producer-config
                                                        "client.id" client-id)))))

(defn close-producer
  [client-id]
  (if-let [producer (get-producer client-id)]
    (do
      (.close ^KafkaProducer producer)
      (swap! producers dissoc client-id))
    (throw (ex-info "No such producer" {:client-id client-id}))))

(defn list-producers
  []
  (keys @producers))

(defn produce!
  {:doc "Helper wrapping `#'send!`.

   Builds and sends a `ProducerRecord` so you don't have to.
   Returns a future which will produce datafied record metadata when forced."
   :arglists '([client-id topic value]
               [client-id topic key value]
               [client-id topic partition key value]
               [client-id topic partition timestamp key value]
               [client-id topic partition timestamp key value headers])}
  [client-id & args]
  (if-let [producer (get-producer client-id)]
    (apply jc/produce! (cons producer args))
    (throw (ex-info "No such producer" {:client-id client-id}))))

(defn close-all-producers
  []
  (doseq [client-id (list-producers)]
    (close-producer client-id)))
