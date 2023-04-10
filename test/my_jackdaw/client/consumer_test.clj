(ns my-jackdaw.client.consumer-test
  (:require [clojure.test :refer :all]
            [my-jackdaw.client.consumer :as sut])
  (:import (clojure.lang ExceptionInfo)))

(def consumer-config
  {"bootstrap.servers" "localhost:9092"
   "group.id" "com.foo.my-consumer"
   "key.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"
   "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"})

(def topic-config
  {:topic-name "jackdaw"})

(deftest consumer-tests
  (testing "can create a consumer"
    (is (= true (contains? (sut/create-consumer "my-consumer" consumer-config topic-config identity) "my-consumer"))))
  (testing "can get a consumer"
    (is (not= nil (sut/get-consumer "my-consumer"))))
  (testing "cannot create the same consumer twice"
    (is (thrown? ExceptionInfo (sut/create-consumer "my-consumer" consumer-config topic-config identity))))
  (testing "can close a consumer"
    (is (sut/stop-consumer "my-consumer"))))

(future-cancel (:process (get @@#'sut/consumers "my-consumer")))

(def f (future 1))

(future-done? f)
