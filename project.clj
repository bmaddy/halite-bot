(defproject hlt "0.1.0-SNAPSHOT"
  :description "FIXME: My halite bot"
  :url "http://example.com/FIXME"
  :license {:name "All Rights Reserved"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 ;; [org.clojure/tools.logging "0.4.0"]
                 ;; [ch.qos.logback/logback-classic "1.1.3"]
                 [com.taoensso/timbre "4.10.0"]
                 ]
  ;; :main ^:skip-aot MyBot
  :main ^:skip-aot system
  :target-path "target/"
  :uberjar-name "MyBot.jar"
  :profiles {:uberjar {:aot :all}})
