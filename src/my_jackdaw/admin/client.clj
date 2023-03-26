(ns my-jackdaw.admin.client
  (:require [jackdaw.admin :as ja]))

; This is a facade!

; todo - aero
(def admin-client-config
  {"bootstrap.servers" "localhost:9092"})

; todo - mount/integrant
(defonce client (ja/->AdminClient admin-client-config))

(defn list-topics
  "Return a seq of topic records, being the topics on the cluster."
  []
  (ja/list-topics client))

(defn delete-topics!
  "Given an `AdminClient` and a sequence of topic descriptors, marks the topics for
  deletion.  Does not block until the topics are deleted, just until the deletion
  request(s) are acknowledged."
  [topics]
  (ja/delete-topics! client topics))

(defn create-topics!
  "Given an `AdminClient` and a collection of topic descriptors, create the specified
  topics with their configuration(s).  Does not block until the created topics are ready.
  It may take some time for replicas and leaders to be chosen for newly created topics.
  See `#'topics-ready?`, `#'topic-exists?` and `#'retry-exists?` for tools with which to
  wait for topics to be ready."
  [topics]
  (ja/create-topics! client topics))

(defn describe-topics
  "Given an optional collection of topic descriptors, return a map from topic names to
  topic descriptions.  If no topics are provided, describes all topics.  Note that the
  topic description does NOT include the topic's configuration.

  See `#'describe-topic-config` for that capability."
  ([]
   (ja/describe-topics client))
  ([topics]
   (ja/describe-topics client topics)))

(defn describe-topic-configs
  "Given a collection of topic descriptors, returns the selected topics' live
  configuration as a map from topic names to configured properties to metadata
  about each property including its current value."
  ([]
   (ja/describe-topics-configs client (ja/list-topics client)))
  ([topics]
   (ja/describe-topics-configs client topics)))

(defn alter-topic-configs
  "Given a sequence of topic descriptors having `:topic-config`, alters the live
  configuration of the specified topics to correspond to the specified `:topic-config`.  "
  ([topics]
   (ja/alter-topic-config! client topics)))

(defn describe-cluster
  []
  (ja/describe-cluster client))

(defn get-broker-config
  [broker-id]
  (ja/get-broker-config client broker-id))

(defn topics-ready?
  [topics]
  (ja/topics-ready? client topics))

(defn partition-ids-of-topics
  [topics]
  (ja/partition-ids-of-topics client topics))

(defn topic-exists?
  ([topic]
   (ja/topic-exists? client topic))
  ([topic num-retries wait-ms]
    (ja/retry-exists? client topic num-retries wait-ms)))

(defn topics-exist?
  ([topics]
   (map #(topic-exists? %) topics))
  ([topics num-retries wait-ms]
   (map #(topic-exists? % num-retries wait-ms) topics)))

(defn client?
  "Return `true` if and only if given an `AdminClient` instance."
  [x]
  (ja/client? x))

(comment

  ; Stuff I'm working on

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

  (type client)

  (type (.metrics client))

  (client-metrics client)

  (map str (.metrics client))

  )
