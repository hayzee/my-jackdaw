(ns user
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.tools.namespace.repl :as tn]
            [clj-figlet.core :as fg]
            [mount.core :as m]
            ;[my-jackdaw.config :as config]
            ;[my-jackdaw.kafka.admin :as admin]
            [taoensso.timbre :as tm]
            [my-jackdaw.config :as cfg]))

(def flf (fg/load-flf (io/resource "big.flf")))

(defn pps
  "pprint to a string"
  [s]
  (with-out-str (pp/pprint s)))

(defn log-banner
  "log output as banner text"
  [lvl s]
  (doseq [line (fg/render flf s)]
    (tm/log lvl line)))

(defn start
  []
  (log-banner :info "kafkastic")
  (m/start)
  (tm/set-min-level! (:log-level cfg/cfg))
  :ready)

(defn stop
  []
  (m/stop)
  #_(tn/refresh :after 'user/start)
  :stopped)

;
;(defn send-to-topic
;  [topic-name k v]
;  (p/send-to-topic topic-name k v))
;
;(defn read-topic
;  [topic-name]
;  (c/read-topic topic-name))
;
;(defn list-topics
;  []
;  (ja/list-topics a/client))
