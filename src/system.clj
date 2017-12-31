(ns system
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre :refer [info]]
            [taoensso.timbre.appenders.core :as appenders]
            [hlt.parser :as parser]
            clojure.pprint
            [hlt.bot :as bot])
  (:gen-class))

;; Turn on file logging
(timbre/merge-config!
 {:appenders {:spit (appenders/spit-appender {:fname "out.log"})
              :println {:enabled? false}}})

(def bot
  (reify bot/IBot
    (get-name [this {:keys [tag] :as state}] (str "basic-bot-" tag))
    (next-moves [this state] [])))

(defn get-line
  []
  (edn/read-string (str \[ (read-line) \])))

(defn start
  [bot]
  (let [[tag] (get-line)
        map-size (get-line)
        state (assoc (parser/build-game-map (get-line))
                     :tag tag
                     :map-size map-size)]
    (info state)
    (info "Initial state loaded.")
    (info {:bot-name (bot/get-name bot state)
           :player-id tag
           :started-at (java.util.Date.)
           :base-dirname "log"})
    (println (bot/get-name bot state))
    )
  #_(log/init {:bot-name :FIXME
             :player-id (:player-id prelude)
             :started-at (java.util.Date.)
             :base-dirname "log"})
  (/ 0)
  #_(io/send-done-initialized io (bot/name state))
  #_(loop [turn 0]
    (log/log :turn turn)
    (try
      (let [state (merge state (io/read-map io))
            moves (bot/next-moves state)]
        (io/send-moves io moves))
      (catch Throwable t
        (with-open [pw (PrintWriter. *out*)]
          (.printStackTrace t pw))
        (throw t)))
    (recur (inc turn))))

(defn -main
  [& args]
  (timbre/log-errors
    (start bot)))
