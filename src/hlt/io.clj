(ns hlt.io
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [hlt.parser :as parser]
            [taoensso.timbre :as timbre]))

;; encoding moves

(def ^:private undock-key "u")
(def ^:private dock-key "d")
(def ^:private thrust-key "t")

(defmulti move-segments :type)

(defmethod move-segments :undock [move]
  [undock-key (-> move :ship :id)])

(defmethod move-segments :dock [move]
  [dock-key (-> move :ship :id) (-> move :planet :id)])

(defmethod move-segments :thrust [move]
  [thrust-key (-> move :ship :id) (:thrust move) (:angle move)])

;; Reading input

(defn get-line
  []
  (edn/read-string (str \[ (read-line) \])))

(defn read-prelude
  []
  (let [[player-id] (get-line)
        map-size (get-line)]
    {:player-id player-id
     :map-size map-size}))

(defn read-map
  []
  (parser/build-game-map (get-line)))

;; Sending commands

(defn send-done-initialized
  "Notifies the game engine that this bot has been initialized."
  [bot-name]
  (println bot-name))

(defn send-moves
  "Sends the moves to the game engine."
  [moves]
  (let [command (->> moves
                     (mapcat move-segments)
                     (str/join " "))]
    (timbre/info (str "Sending command: " command))
    (println command)))
