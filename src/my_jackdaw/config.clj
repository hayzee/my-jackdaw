(ns my-jackdaw.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [cprop.core :refer [load-config]]
            [cprop.source :as src :refer [from-system-props
                                          from-env]]
            [mount.core :refer [defstate]]))

(defstate cfg
          :start (load-config ;:resource "config.edn"
                              :file "resources/config.edn"))

