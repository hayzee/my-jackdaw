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
  [client-id topic key value]
  (if-let [producer (get-producer client-id)]
    (jc/produce! producer topic key value)
    (throw (ex-info "No such producer" {:client-id client-id}))))

(defn close-all-producers
  []
  (doseq [client-id (list-producers)]
    (close-producer client-id)))
