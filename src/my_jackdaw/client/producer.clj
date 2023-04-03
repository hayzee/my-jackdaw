(ns my-jackdaw.client.producer
  (:require [jackdaw.client :as jc])
  (:import (org.apache.kafka.clients.producer KafkaProducer)))

; PRODUCER

(defonce ^:private producers (atom {}))

(def producer-config
  {"bootstrap.servers" "localhost:9092"
   "key.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "value.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "acks" "all"
   "client.id" "bob2"})

(defn create-producer
  [producer-name producer-config]
  (if (get (deref producers) name)
    (ex-info "A producer of this name already exists" {:name name})
    (swap! producers assoc producer-name (jc/producer producer-config))))

(defn list-producers
  []
  (keys @producers))

(defn get-producer
  [producer-name]
  (get @producers producer-name))

(defn produce!
  [producer-name topic key value]
  (if-let [producer (get @producers producer-name)]
    (jc/produce! producer topic key value)
    (throw (ex-info "No such producer" {:producer-name producer-name}))))

(defn close-producer
  [producer-name]
  (let [producer (get @producers producer-name)]
    (.close ^KafkaProducer producer)
   (swap! producers dissoc producer-name)))

(defn close-producers
  []
  (doseq [producer-name (list-producers)]
    (close-producer producer-name)))

