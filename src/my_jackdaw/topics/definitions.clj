(ns my-jackdaw.topics.definitions
  (:require [jackdaw.serdes :as serdes]))

(defmacro deftopic
  [topic-name]
  `(def ~topic-name {:topic-name         ~(str topic-name)
                     :partition-count    3
                     :replication-factor 1
                     :key-serde          (serdes/edn-serde)
                     :value-serde        (serdes/edn-serde)}))

(deftopic input)
(deftopic output)
