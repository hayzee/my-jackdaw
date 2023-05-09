(ns my-jackdaw.kafka.admin
  (:require [jackdaw.admin :as ja]))

(def client-config
  {"bootstrap.servers" "localhost:9092"})

(defonce client (ja/->AdminClient client-config))

