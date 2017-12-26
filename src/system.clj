(ns system
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre :refer [info]]
            [taoensso.timbre.appenders.core :as appenders]
            [hlt.entity :as e]
            clojure.pprint)
  (:gen-class))

;; ;; Turn on file logging
;; (log/set-config! [:appenders :spit :enabled?] true)
;; ;; Set the log file location
;; (log/set-config! [:shared-appender-config :spit-filename] "out.log")
(timbre/merge-config!
 {:appenders {:spit (appenders/spit-appender {:fname "out.log"})
              :println {:enabled? false}}})

(def basic-bot
  {:name "Basic"})

(defn get-line
  []
  (edn/read-string (str \[ (read-line) \])))

(def docking-status-id->docking-status
  [:undocked :docking :docked :undocking])

(defn get-ship
  [owner-id
   [id
    x y
    health
    _ ;; x-velocity is deprecated
    _ ;; y-velocity is deprecated
    docking-status-id
    docked-planet-id
    docking-progress
    weapon-cooldown
    & remaining]]
  (let [ship {:id id
              :owner-id owner-id
              :position [x y]
              :radius e/ship-radius
              :health health
              :docking-status (docking-status-id->docking-status docking-status-id)
              :docked-planet-id docked-planet-id
              :weapon-cooldown weapon-cooldown}]
    [ship remaining]))

(defn get-player
  [[player-id ship-count & data]]
  (loop [ship-num 0
         data data
         player {:id player-id
                 :ships []}]
    (info {:ship-num ship-num
           :data data})
    (if (= ship-num ship-count)
      [player data]
      (let [[ship remaining] (get-ship player-id data)]
        (info {:ship ship})
        (recur (inc ship-num)
               remaining
               (update player :ships conj ship))))))

(defn build-game-map
  [[player-count & data :as tokens]]
  (info {:player-count player-count})
  (loop [player-num 0
         data data
         game-map {:ships-by-player-id {}}]
    (info {:player-num player-num
           :data data})
    (if (= player-num player-count)
      game-map
      (let [[player remaining] (get-player data)]
        (info {:player player})
        (recur (inc player-num)
               remaining
               (update game-map :ships-by-player-id assoc (:id player) (:ships player)))))))

(defn start
  [bot]
  (let [[tag] (get-line)
        _ (info {:tag tag})
        map-size (get-line)
        _ (info {:map-size map-size})
        state (assoc (build-game-map (get-line))
                     :tag tag
                     :map-size map-size)]
    (info state))
  (info {:bot-name :FIXME
         :player-id :player-id
         :started-at (java.util.Date.)
         :base-dirname "log"})
  (info "Initial state loaded.")
  #_(let [io {:in *in* :out *out*}
        prelude (io/read-prelude io)
        initial-map (io/read-map io)
        state (merge prelude initial-map)]
    (log/init {:bot-name :FIXME
               :player-id (:player-id prelude)
               :started-at (java.util.Date.)
               :base-dirname "log"})
    (io/send-done-initialized io (bot/name state))
    (loop [turn 0]
      (log/log :turn turn)
      (try
        (let [state (merge state (io/read-map io))
              moves (bot/next-moves state)]
          (io/send-moves io moves))
        (catch Throwable t
          (with-open [pw (PrintWriter. *out*)]
            (.printStackTrace t pw))
          (throw t)))
      (recur (inc turn)))))

(defn -main
  [& args]
  (start {}))
