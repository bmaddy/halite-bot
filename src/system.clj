(ns system
  (:require [clojure.edn :as edn]
            [hlt.io :as io]
            [taoensso.timbre :as timbre :refer [info]]
            [taoensso.timbre.appenders.core :as appenders]
            [hlt.parser :as parser]
            clojure.pprint
            [hlt.bot :as bot]
            [hlt.entity :as e]
            [hlt.navigation :as navigation])
  (:gen-class))

;; Turn on file logging
(timbre/merge-config!
 {:appenders {:spit (appenders/spit-appender {:fname "out.log"})
              :println {:enabled? false}}})

(defn compute-move
  [ship state]
  (when (= (-> ship :docking :status) :undocked)
    (first
     (for [planet (-> state :planets-by-id vals)
           :when (-> planet :owner-id nil?)]
       (if (e/within-docking-range? ship planet)
         (e/dock-move ship planet)
         (navigation/navigate-to-dock ship planet state))))))

(def bot
  (reify bot/IBot
    (get-name [this {:keys [player-id] :as state}] (str "basic-bot-" player-id))
    (next-moves [this {:keys [player-id] :as state}]
      (let [ships (get-in state [:ships-by-player-id player-id])]
        (keep #(compute-move % state) ships)))))

(defn start
  [bot]
  (let [
        ;; [player-id] (get-line)
        ;; map-size (get-line)
        ;; state (assoc (parser/build-game-map (get-line))
        ;;              :player-id player-id
        ;;              :map-size map-size)
        state (merge (io/read-prelude)
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
      (recur (inc turn))))
  #_(loop [turn 0]
    (log/log :turn turn)
    (let [state (merge state (io/read-map io))
          moves (bot/next-moves state)]
      (io/send-moves io moves))
    (recur (inc turn))))

(defn -main
  [& args]
  (timbre/log-errors
    (start bot)))
