(ns my-jackdaw.client.producer-test
  (:require [clojure.test :refer :all]
            [my-jackdaw.client.producer :as sut]))

(def producer-config
  {"bootstrap.servers" "localhost:9092"
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
    (is (thrown? clojure.lang.ExceptionInfo (sut/create-producer "my-producer" producer-config))))
  (testing "can close (and remove) a producer"
    (is (= false (contains? (sut/close-producer "my-producer") "my-producer")))))

(deftest producer-tests
  (sut/create-producer "my-producer" producer-config)
  (sut/create-producer "my-producer-2" producer-config)
  (sut/create-producer "my-producer-3" producer-config)
  (testing "can list producers"
    (is (= 3 (count (sut/list-producers)))))
  (testing "can close (and remove) all producers"
    (sut/close-all-producers)
    (is (= 0 (count (sut/list-producers))))))

