(ns my-jackdaw.client.consumer-test
  (:require [clojure.test :refer :all])
  (:require [my-jackdaw.client.consumer :refer :all]))

(def consumer-config
  {"bootstrap.servers" "localhost:9092"
   "group.id" "com.foo.my-consumer"
   "key.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"
   "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"})

(def topic-config
  {:topic-name "jackdaw"})

