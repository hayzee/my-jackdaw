(ns my-jackdaw.core
  (:import (java.io File)
           (java.util Properties)))

(defn timeout [timeout-ms callback]
  (let [fut (future (callback))
        ret (deref fut timeout-ms ::timed-out)]
    (when (= ret ::timed-out)
      (future-cancel fut))
    ret))

(defn props->map
  [^Properties p]
  (->> p
       (into {})
       clojure.walk/keywordize-keys))

(defn map->props
  [m]
  (doto (Properties.)
    (.putAll (clojure.walk/stringify-keys m))))

(props->map
  (map->props
    {:this (new File "iuhkkjh")
     :that "Sausage"}))

(defn merge-props
  [^Properties p1 ^Properties p2]
  (.putAll p1 p2)
  p1)

(props->map
  (merge-props
    (map->props {:this :that :the :other})
    (map->props {:mains :fish :pud :ice-cream :the :banana})))

(def all-words (clojure.string/split-lines (slurp "https://raw.githubusercontent.com/dwyl/english-words/master/words_alpha.txt")))
(def all-words-grouped (group-by count all-words))

(sort-by first (map #(vector (first %) (count (second %))) all-words-grouped))
