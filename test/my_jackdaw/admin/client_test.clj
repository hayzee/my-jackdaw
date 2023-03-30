(ns my-jackdaw.admin.client-test
  (:require [clojure.test :refer :all]
            [my-jackdaw.admin.client :as sut]))

(def topics-to-create
  [{:topic-name         "jackdaw"
    :partition-count    5
    :replication-factor 1
    :delete-topic-enable true
    :topic-config       {"cleanup.policy" "compact"}}
   {:topic-name         "banana"
    :partition-count    5
    :replication-factor 1
    :delete-topic-enable true
    :topic-config       {"cleanup.policy" "compact"}}
   {:topic-name         "apples"
    :partition-count    5
    :replication-factor 1
    :delete-topic-enable true
    :topic-config       {"cleanup.policy" "compact"}}])

(defn clear-topics-fixture
  [f]
  (println "set-up deleting topics")
  (sut/delete-topics! (sut/list-topics))
  (f)
  (println "clear-down deleting topics")
  (sut/delete-topics! (sut/list-topics)))

(use-fixtures :once clear-topics-fixture)

(deftest create-list-and-delete-topics-test

  (testing "Can create topics"
    (sut/create-topics! topics-to-create))

  (testing "Can list topics"
    (is (= [{:topic-name "apples"}
            {:topic-name "banana"}
            {:topic-name "jackdaw"}] (sut/list-topics))))

  (testing "Can describe topics"
    (is (= 3 (count (sut/describe-topics)))))

  (testing "Can describe topic configs"
    (is (= 3 (count (sut/describe-topic-configs)))))

  (testing "Can alter topic configs"
    (sut/alter-topic-configs [{:topic-name          "apples"
                               :topic-config        {"cleanup.policy" "delete"}}])
    (is (= "delete"
           (-> (sut/describe-topic-configs [{:topic-name "apples"}])
               first
               val
               (get "cleanup.policy")
               :value))))

  (testing "Can describe cluster"
    (is 3 (count (keys (sut/describe-cluster)))))

  (testing "Can get broker config"
    (is 219 (count (keys (sut/get-broker-config 1)))))

  (testing "Can test topics ready"
    (is (= true (sut/topics-ready? (sut/list-topics)))))

  (testing "Can get topic partition ids"
    (is (= {"banana" [0 1 2 3 4], "jackdaw" [0 1 2 3 4], "apples" [0 1 2 3 4]}
           (sut/partition-ids-of-topics (sut/list-topics)))))

  (testing "Can test for topic existence"
    (is (= true
           (sut/topic-exists? (first (sut/list-topics))))))

  (testing "Can test for many topic's existence"
    (is (= true
           (sut/topics-exist? (sut/list-topics)))))

  (testing "Can delete topics"
    (sut/delete-topics! (sut/list-topics))
    (is (= [] (sut/list-topics))))

  (testing "Can test for a client"
    (is (= true (sut/client? sut/client)))))
