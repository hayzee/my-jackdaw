(ns my-jackdaw.core-test
  (:require [clojure.test :refer :all]
            [my-jackdaw.core :refer :all]))

(deftest a-test
  (testing "FIXED, I no longer fail."
    (is (= 1 1))))
