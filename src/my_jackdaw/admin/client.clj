(ns my-jackdaw.admin.client
  (:require [mount.core :refer [defstate]]
            [jackdaw.admin :as ja]))

(defstate admin-client-config
          :start {"bootstrap.servers" "localhost:9092"})

(defn create-client
  [admin-client-config]
  (ja/->AdminClient admin-client-config))

(defstate client
          :start (create-client admin-client-config)
          :stop (.close client))

(defn running?
  []
  (my-jackdaw.core/timeout 1000
                           #(ja/describe-cluster client)))

(defn delete-topics!
  []
  (println "delete all topics")
  (ja/delete-topics! client (ja/list-topics client)))

(defn create-topics!
  "Create topics from one or more topic configurations."
  [& topic-configs]
  (ja/create-topics! client topic-configs))

(defn list-topics
  "List all topics."
  []
  (ja/list-topics client))

(defn describe-topics
  "Describe the topics listed in topic names. If no topic-names are provided,
  descriptions for all topics are returned.

  e.g. (describe-topics \"my-topic\" \"my-other-topic\" ...
  "
  [& topic-names]
  (ja/describe-topics client (doto (map (fn [name] {:topic-name name}) topic-names) clojure.pprint/pprint)))


;(mount.core/start)
;(describe-topics "jackdaw" "jackdaw")


(comment
  ;(ja/describe-topics-configs client [{:topic-name "jackdaw"}])


  (ja/alter-topic-config! client [{:topic-name   "jackdaw"
                                   :topic-config {"delete.retention.ms" "1000"}}])

  (ja/describe-cluster client)

  (ja/get-broker-config client 1)

  (ja/partition-ids-of-topics client #_(ja/list-topics client))

  ; can nest function calls - e.g. delete all topics
  ;(ja/delete-topics! client (ja/list-topics client))

  ; or can delete individual topics
  ; (ja/delete-topics! client [{:topic-name "jackdaw"}])

  ; not sure what this does.
  ;(ja/alter-topics* client {:topic-name "jackdaw"
  ;                          :partition-count 21
  ;                          :replication-factor 1
  ;                          :topic-config {"cleanup.policy" "compact"}})

  (defn get-consumer-groups
    [client]
    (mapv
      (fn [cgl]
        {:group-id                 (.groupId cgl)
         :is-simple-consumer-group (.isSimpleConsumerGroup cgl)}
        )
      @(.all (.listConsumerGroups client))))

  (get-consumer-groups client)

  (defn client-metrics
    [client]
    (map (fn [me]
           {:name        (.name (.getKey me))
            :description (.description (.metricName (.getValue me)))
            :datatype    (type (.metricValue (.getValue me)))
            :value       (.metricValue (.getValue me))
            }) (.metrics client)))

  (client-metrics client)

  )
