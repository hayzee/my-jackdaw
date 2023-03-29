(ns my-jackdaw.client.producer
  (:require [jackdaw.client :as jc]))

; PRODUCER

(def producers (atom {}))

(def producer-config
  {"bootstrap.servers" "localhost:9092"
   "key.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "value.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "acks" "all"
   "client.id" "some-client"})

(defn create-producer
  [name producer-config]
  (if (get (deref producers) name)
    (ex-info "A producer of this name already exists" {:name name})
    (swap! producers assoc name (jc/producer producer-config))))

(defn produce!
  [producer-name topic key value]
  (let [producer (get @producers producer-name)]
    (jc/produce! producer topic key value)))

(defn close-producer
  [producer-name]
  (let [producer (get @producers producer-name)]
   (.close producer)))
