(ns my-jackdaw.kafka.producer-test
  (:require [clojure.test :refer :all]
            [my-jackdaw.kafka.admin :as ac]
            [my-jackdaw.kafka.producer :as sut])
  (:import (clojure.lang ExceptionInfo)))

(def producer-config
  {"bootstrap.servers" "localhost:9094"
   "key.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "value.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "acks" "all"
   "client.id" "default"})

(deftest producer-tests
  (testing "can create a producer"
    (is (= true (contains? (sut/create-producer "my-producer" producer-config) "my-producer"))))
  (testing "can get a producer"
    (is (not= nil (sut/get-producer "my-producer"))))
  (testing "cannot create the same producer twice"
    (is (thrown? ExceptionInfo (sut/create-producer "my-producer" producer-config))))
  (testing "can close (and remove) a producer"
    (is (= false (contains? (sut/close-producer "my-producer") "my-producer")))))

(deftest multi-producer-tests
  (sut/create-producer "my-producer" producer-config)
  (sut/create-producer "my-producer-2" producer-config)
  (sut/create-producer "my-producer-3" producer-config)
  (testing "can list producers"
    (is (= 3 (count (sut/list-producers)))))
  (testing "can close (and remove) all producers"
    (sut/close-all-producers)
    (is (= 0 (count (sut/list-producers))))))

(deftest producer-producing-tests
  (testing "can create a producer"
    (sut/create-producer "some-producer" producer-config)
    (is (= {:topic-name "test-topic",
            :partition 0,
            :serialized-key-size 4,
            :serialized-value-size 4}
           (-> @(sut/produce! "some-producer" {:topic-name "test-topic"} "key1" "val1")
               (select-keys [:topic-name :partition :serialized-key-size :serialized-value-size]))))
    (sut/close-all-producers)
    (jackdaw.admin/delete-topics! ac/client [{:topic-name "test-topic"}])))
