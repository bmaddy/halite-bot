(defproject hlt "0.1.0-SNAPSHOT"
  :description "FIXME: My halite bot"
  :url "http://example.com/FIXME"
  :license {:name "All Rights Reserved"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [org.clojure/math.combinatorics "0.1.4"]]
  :main ^:skip-aot MyBot
  :target-path "target/"
  :uberjar-name "MyBot.jar"
  :profiles {:uberjar {:aot :all}})
