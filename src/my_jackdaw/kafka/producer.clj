(ns my-jackdaw.kafka.producer
  (:require [jackdaw.client :as jc]
            [my-jackdaw.kafka.admin :as admin])
  (:import (org.apache.kafka.clients.producer KafkaProducer)
           (org.apache.kafka.common.utils Bytes)))

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
  "Helper wrapping `#'send!`.

  Builds and sends a `ProducerRecord` so you don't have to.
  Returns a future which will produce datafied record metadata when forced."
  {:arglists '([client-id topic value]
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

(defn send-to-topic
  "Sends edn 'k'ey and 'v'alues to topic."
  [topic-name k v]
  (let [producer-config
        (into admin/client-config
              {"key.serializer"   "org.apache.kafka.common.serialization.BytesSerializer"
               "value.serializer" "org.apache.kafka.common.serialization.BytesSerializer"
               "acks"             "all"
               "client.id"        "send-to-topic"})]
    (with-open [my-producer (jc/producer producer-config)]
      @(jc/produce! my-producer
                    {:topic-name topic-name}
                    (new Bytes (.getBytes (pr-str k)))
                    (new Bytes (.getBytes (pr-str v)))))))
