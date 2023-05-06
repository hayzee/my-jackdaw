(defproject my-jackdaw "0.1.0-SNAPSHOT"

  :description "FIXME: write description"

  :url "http://example.com/FIXME"

  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.11.1"]

                 ; kafka / jackdaw
                 [fundingcircle/jackdaw "0.9.8"]
                 [org.apache.kafka/kafka-streams-test-utils "2.8.0"]

                 ; logging
                 [com.fzakaria/slf4j-timbre "0.3.21"]
                 [com.taoensso/timbre "6.1.0"]

                 ; components
                 [mount "0.1.17"]

                 ;config
                 [aero "1.1.6"]]

  ; :repl-options {:init-ns my-jackdaw.core}

  )
