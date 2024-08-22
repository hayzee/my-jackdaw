(ns my-jackdaw.kafka.admin
  (:require [jackdaw.admin :as ja]
            [mount.core :refer [defstate]]
            [my-jackdaw.config :as config]
            [taoensso.timbre :as tm]))
;
(defstate client
  :start (do
           (tm/log :info "Starting AdminClient")
           (ja/->AdminClient (-> config/cfg
                                 :kafka-config)))
  :stop (.close client))

(defn client-running?
  "`true` if and only if given an `AdminClient` instance."
  []
  (if (ja/client? client)
    true
    (do
      (tm/error "Client is not running")
      false)))

(defn delete-topics!
  "Given an `AdminClient` and a sequence of topic descriptors, marks the
  topics for deletion.

  Does not block until the topics are deleted, just until the deletion
  request(s) are acknowledged."
  [topics]
  (when (client-running?)
    (ja/delete-topics! client topics)))

(defn describe-topics
  "Given an `AdminClient` and an optional collection of topic
   descriptors, return a map from topic names to topic
   descriptions.

   If no topics are provided, describes all topics.

   Note that the topic description does NOT include the topic's
   configuration.See `#'describe-topic-config` for that capability."
  ([]
   (when (client-running?)
     (ja/describe-topics client)))
  ([topics]
   (when (client-running?)
     (ja/describe-topics client topics))))

(defn partition-ids-of-topics
  "Given an `AdminClient` and an optional sequence of topics, produces a
  mapping from topic names to a sequence of the partition IDs for that
  topic.

  By default, enumerates the partition IDs for all topics."
  ([]
   (when (client-running?)
     (ja/partition-ids-of-topics client)))
  ([topics]
   (when (client-running?)
     (ja/partition-ids-of-topics client topics))))

(defn list-topics
  "Given an `AdminClient`, return a seq of topic records, being the
  topics on the cluster."
  []
  (when (client-running?)
    (ja/list-topics client)))

(defn create-topics!
  "Given an `AdminClient` and a collection of topic descriptors,
  create the specified topics with their configuration(s).

  Does not block until the created topics are ready. It may take some
  time for replicas and leaders to be chosen for newly created
  topics.

  See `#'topics-ready?`, `#'topic-exists?` and `#'retry-exists?` for
  tools with which to wait for topics to be ready."
  [topics]
  (when (client-running?)
    (ja/create-topics! client topics)))

(defn retry-exists?
  "Returns `true` if topic exists. Otherwise spins as configured."
  [topic num-retries wait-ms]
  (when (client-running?)
    (ja/retry-exists? client topic num-retries wait-ms)))

(defn get-broker-config
  "Returns the broker config as a map.

  Broker-id is an int, typically 0-2, get the list of valid broker ids
  using describe-cluster"
  [broker-id]
  (when (client-running?)
    (ja/get-broker-config client broker-id)))

(defn describe-cluster
  "Returns a `DescribeClusterResult` describing the cluster."
  []
  (when (client-running?)
   (ja/describe-cluster client)))

(defn describe-topics-configs
  "Given an `AdminClient` and a collection of topic descriptors, returns
  the selected topics' live configuration as a map from topic names to
  configured properties to metadata about each property including its
  current value."
  [topics]
  (when (client-running?)
    (ja/describe-topics-configs client topics)))

(defn alter-topic-config!
  "Given an `AdminClient` and a sequence of topic descriptors having
  `:topic-config`, alters the live configuration of the specified
  topics to correspond to the specified `:topic-config`."
  [topics]
  (when (client-running?)
    (ja/alter-topic-config! client topics)))

(defn topic-exists?
  "Verifies the existence of the topic.

  Does not verify any config. details or values."
  [topic]
  (when (client-running?)
    (ja/topic-exists? client topic)))

(defn topics-ready?
  "Given an `AdminClient` and a sequence topic descriptors, return
  `true` if and only if all listed topics have a leader and in-sync
  replicas.

  This can be used to determine if some set of newly created topics
  are healthy yet, or detect whether leader re-election has finished
  following the demise of a Kafka broker."
  [topics]
  (when (client-running?)
    (ja/topics-ready? client topics)))

(comment

  {'delete-topics! #'jackdaw.admin/delete-topics!,
   'describe-topics #'jackdaw.admin/describe-topics,
   'partition-ids-of-topics #'jackdaw.admin/partition-ids-of-topics,
   ;'alter-topics* #'jackdaw.admin/alter-topics*,
   ;'delete-topics* #'jackdaw.admin/delete-topics*,
   'client? #'jackdaw.admin/client?,
   ;'list-topics* #'jackdaw.admin/list-topics*,
   ;'topics->configs #'jackdaw.admin/topics->configs,  ;; private
   'list-topics #'jackdaw.admin/list-topics,
   'create-topics! #'jackdaw.admin/create-topics!,
   ;'create-topics* #'jackdaw.admin/create-topics*,
   ;'describe-topics* #'jackdaw.admin/describe-topics*,
   'retry-exists? #'jackdaw.admin/retry-exists?,
   'get-broker-config #'jackdaw.admin/get-broker-config,
   ;'client-impl #'jackdaw.admin/client-impl,
   ;'describe-cluster* #'jackdaw.admin/describe-cluster*,
   ;'describe-configs* #'jackdaw.admin/describe-configs*,
   'describe-cluster #'jackdaw.admin/describe-cluster,
   'describe-topics-configs #'jackdaw.admin/describe-topics-configs,
   'alter-topic-config! #'jackdaw.admin/alter-topic-config!,
   ;'->AdminClient #'jackdaw.admin/->AdminClient,
   ;'Client #'jackdaw.admin/Client,
   'topic-exists? #'jackdaw.admin/topic-exists?,
   'topics-ready? #'jackdaw.admin/topics-ready?}

  )
