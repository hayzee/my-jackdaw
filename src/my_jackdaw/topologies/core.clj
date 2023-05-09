(ns my-jackdaw.topologies.core
  (:require
    [clojure.string :as str]
    [my-jackdaw.kafka.admin :as admin]
    [jackdaw.admin :as ja]
    [jackdaw.streams :as j]
    [jackdaw.serdes.edn :as edn]
    [jackdaw.serdes :as serdes]))

(def topic-metadata

  {:input
   {:topic-name "input"
    :partition-count 1
    :replication-factor 1
    :key-serde (serdes/edn-serde)
    :value-serde (serdes/edn-serde)}

   :output
   {:topic-name "output"
    :partition-count 1
    :replication-factor 1
    :key-serde (serdes/edn-serde)
    :value-serde (serdes/edn-serde)}})

(comment

 (ja/create-topics! admin/client (map second topic-metadata))

 (ja/list-topics admin/client)

 (ja/delete-topics! admin/client (ja/list-topics admin/client))

 )


(defn split-lines
  [input-string]
  (str/split (str/lower-case input-string) #"\W+"))

(defn topology-builder
  [topic-metadata]
  (fn [builder]
    (let [text-input (j/kstream builder (:input topic-metadata))

          counts (-> text-input
                     (j/flat-map-values split-lines)
                     (j/group-by (fn [[_ v]] v))
                     (j/count))]

      (-> counts
          (j/to-kstream)
          (j/to (:output topic-metadata)))

      builder)))



(defn -main
  [& args]
  (let [app-config (into {
                          ;"client-id" (str "client:" (str *ns*))
                          "application.id" (str *ns*)
;                          "state.dir" "C:\\X-Drive\\temp\\kafka-streams"
;                          "state.dir" "~/kafka-streams"
                          "default.key.serde" (class (serdes/string-serde))
                          "default.value.serde" (class (serdes/string-serde))
                          ;                          :value-serde (serdes/edn-serde)
                          }
                         admin/client-config)

        builder (j/streams-builder)
        topology ((topology-builder topic-metadata) builder)
        app (j/kafka-streams topology app-config)]
    (j/start app)
    app))

(comment

  (def app (-main))

  (.state app)

  (j/close app)

  )


