(ns MyBot
  (:gen-class)
  (:require [hlt.bot :as bot]
            [hlt.io :as io]
            bot-impl
            [taoensso.timbre :as timbre :refer [info]]
            [taoensso.timbre.appenders.core :as appenders]))

;; Turn on file logging
(timbre/merge-config!
 {:appenders {:spit (appenders/spit-appender {:fname "out.log"})
              :println {:enabled? false}}})

(defn start
  [bot]
  (let [state (merge (io/read-prelude)
                     (io/read-map))]
    (info state)
    (info "Initial state loaded.")
    (info {:bot-name (bot/get-name bot state)
           :started-at (java.util.Date.)
           :base-dirname "log"})

    ;; respond with the bot's name
    (io/send-done-initialized (bot/get-name bot state))

    (loop [turn 0]
      (info (str "Turn " turn " started."))
      (let [moves (->> (io/read-map)
                       (merge state)
                       (bot/next-moves bot))]
        (info "Sending moves: " (vec moves))
        (io/send-moves moves))
      (info (str "Turn " turn " finished."))
      (recur (inc turn)))))

(defn -main
  [& args]
  (timbre/log-errors
    (start bot-impl/bot)))
