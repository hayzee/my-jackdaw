(ns my-jackdaw.admin.client-test
  (:require [clojure.test :refer :all]
            [jackdaw.admin :as ja]
            [my-jackdaw.admin.client :as sut]))

(defn with-admin-client
  [f]
  (with-redefs [sut/client (ja/->AdminClient {"bootstrap.servers" "localhost:9092"})]
    (try
     (f)
     (finally
       (println "closing client")
       (.close sut/client)))))

;(use-fixtures :once)
(use-fixtures :each with-admin-client)

(deftest create-topics!-test

  (sut/delete-topics!)

;  (while (seq (sut/list-topics)))
  (Thread/sleep 200)

  (is (= '() (sut/list-topics)))

  (is (= {:topic-name         "jackdaw"
          :partition-count    5
          :replication-factor 1
          :topic-config       {"cleanup.policy" "compact"}}
         (sut/create-topics!
               {:topic-name         "jackdaw"
                :partition-count    5
                :replication-factor 1
                :topic-config       {"cleanup.policy" "compact"}}))))
