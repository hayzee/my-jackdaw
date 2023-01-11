(ns my-jackdaw.admin.client
  (:require [jackdaw.admin :as ja]))

; Docker kafka setup:

; https://hub.docker.com/r/bitnami/kafka/

; https://lankydan.dev/running-kafka-locally-with-docker

; docker-compose.yml:

;version: "3"
;services:
;  zookeeper:
;    image: 'bitnami/zookeeper:latest'
;    ports:
;      - '2181:2181'
;    environment:
;      - ALLOW_ANONYMOUS_LOGIN=yes
;kafka:
;  image: 'bitnami/kafka:latest'
;  ports:
;    - '9092:9092'
;  environment:
;    - KAFKA_BROKER_ID=1
;    - KAFKA_LISTENERS=PLAINTEXT://:9092
;    - KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://127.0.0.1:9092
;    - KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181
;    - ALLOW_PLAINTEXT_LISTENER=yes
;  depends_on:
;    - zookeeper

; to start
; --------
; $ docker-compose up -d

; run a shell in a docker container
; ---------------------------------
; $ docker exec -it e3c52ccf6deb sh                  ; replace e3c52ccf6deb with actual image name.

; when finished
; -------------
; $ docker-compose down --remove-orphans

(def client (ja/->AdminClient {"bootstrap.servers" "localhost:9092"}))

(ja/create-topics! client [{:topic-name "jackdaw"
                            :partition-count 15
                            :replication-factor 1
                            :topic-config {"cleanup.policy" "compact"}}])

(ja/list-topics client)

(ja/describe-topics client [{:topic-name "jackdaw"}])

(ja/describe-topics-configs client [{:topic-name "jackdaw"}])

(ja/alter-topic-config! client [{:topic-name "jackdaw"
                                 :topic-config {"delete.retention.ms" "1000"}}])

(ja/describe-cluster client)

(ja/get-broker-config client 1)

(ja/partition-ids-of-topics client #_(ja/list-topics client))

; can nest function calls - e.g. delete all topics
(ja/delete-topics! client (ja/list-topics client))

; or can delete individual topics
; (ja/delete-topics! client [{:topic-name "jackdaw"}])

; not sure what this does.
;(ja/alter-topics* client {:topic-name "jackdaw"
;                          :partition-count 21
;                          :replication-factor 1
;                          :topic-config {"cleanup.policy" "compact"}})
