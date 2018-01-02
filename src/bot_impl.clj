(ns bot-impl
  (:require [hlt.entity :as e]
            [hlt.navigation :as nav]
            [hlt.bot :as bot]
            [clojure.math.combinatorics :as combo]
            [taoensso.timbre :as timbre]))

(defn compute-move
  [ship state]
  (when (= (-> ship :docking :status) :undocked)
    (first
     (for [planet (-> state :planets-by-id vals)
           :when (-> planet :owner-id nil?)]
       (if (e/within-docking-range? ship planet)
         (e/dock-move ship planet)
         (nav/navigate-to-dock ship planet state))))))

(defn default-strategy
  [{:keys [player-id] :as state}]
  (let [ships (get-in state [:ships-by-player-id player-id])]
    (keep #(compute-move % state) ships)))

(defn all-pairings
  "Returns all possible pairings of ships and planets."
  [ships planets]
  (set
   (for [ss (combo/permutations ships)
         ps (combo/permutations planets)]
     (zipmap ss ps))))

(defn target-planet
  [state ship planet]
  (if (e/within-docking-range? ship planet)
    (e/dock-move ship planet)
    (nav/navigate-to-dock ship planet state)))

(defn nearest-planet-first
  [{:keys [player-id] :as state}]
  (let [ships (get-in state [:ships-by-player-id player-id])
        grouped-planets (->> state :planets-by-id vals (group-by (comp boolean :has-owner)))
        [owned unowned] (map grouped-planets [true false])]
    (remove nil? (map (partial target-planet state) ships (cycle unowned)))))

;; get all ship/planet combinations for undocked ships
;; min-by sum of the distance
;; build the moves

(def bot
  (reify bot/IBot
    (get-name [this {:keys [player-id] :as state}] (str "basic-bot-" player-id))
    (next-moves [this {:keys [player-id] :as state}] (nearest-planet-first state))))
