(defproject my-jackdaw "0.1.0-SNAPSHOT"

  :description "FIXME: write description"

  :url "http://example.com/FIXME"

  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.11.1"]

                 ; kafka / jackdaw
                 [fundingcircle/jackdaw "0.9.11"]
                 [org.apache.kafka/kafka-streams-test-utils "3.3.2"]

                 ; logging
                 [com.fzakaria/slf4j-timbre "0.3.21"]
                 [com.taoensso/timbre "6.1.0"]

                 ; components
                 ; [mount "0.1.17"]

                 ; config
                 ; [aero "1.1.6"]
                 ; [cprop "0.1.19"]

                 ]

  ; :repl-options {:init-ns my-jackdaw.core}

  :profiles
  {:uberjar {:omit-source true
             :aot :all
             :uberjar-name "my-jackdaw.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]
             :injections [
                          (println "Running with uberjar profile!")
                          ]
             }

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {; :jvm-opts ["-Dconf=dev-config.edn"]  ; see https://github.com/tolitius/cprop
                  :dependencies [[org.apache.kafka/kafka-streams-test-utils "3.3.2"]]

                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [
                               (println "Running with dev profile!")
                               ]
                  }
   :project/test {; :jvm-opts ["-Dconf=test-config.edn"]  ; see https://github.com/tolitius/cprop

                  :resource-paths ["env/test/resources"]
                  :injections [
                               (println "Running with test profile!")
                               ]
                  }
   :profiles/dev {}
   :profiles/test {}})
