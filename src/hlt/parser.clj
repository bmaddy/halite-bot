(ns hlt.parser
  (:require [hlt.entity :as e]
            [hlt.math :as math]))

(def docking-status-id->docking-status
  [:undocked :docking :docked :undocking])

(defn get-entities
  [get-entity [entity-count & data]]
  (loop [entity-num 0
         data data
         entities []]
    (if (= entity-num entity-count)
      [entities data]
      (let [[entity remaining] (get-entity data)]
        (recur (inc entity-num)
               remaining
               (conj entities entity))))))

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
  (let [ship (e/->Ship id (math/->Position x y) health e/ship-radius owner-id
                       {:status (docking-status-id->docking-status docking-status-id)
                        :planet-id docked-planet-id
                        :progress docking-progress})]
    [ship remaining]))

(defn get-player
  [[player-id & data]]
  (let [[ships remaining] (get-entities #(get-ship player-id %) data)]
    [{:id player-id :ships ships} remaining]))

(defn get-planet
  [[id
    x y
    health
    radius
    docking-spot-count
    current-production
    _ ;; remaining-production is deprecated
    has-owner
    owner-candidate
    docked-ship-count
    & data]]
  (let [[docked-ship-ids remaining] (split-at docked-ship-count data)
        planet (e/->Planet id (math/->Position x y) health radius
                           (case has-owner
                             0 nil
                             1 owner-candidate)
                           {:spots docking-spot-count
                            :ship-ids docked-ship-ids})]
    [planet remaining]))

(defn build-game-map
  [data]
  (let [[ships remaining] (get-entities get-player data)
        [planets _] (get-entities get-planet remaining)
        ships-by-player-id (reduce (fn [acc {:keys [id ships]}]
                                     (assoc acc id ships))
                                   {}
                                   ships)
        planets-by-id (reduce (fn [acc {:keys [id] :as planet}]
                                (assoc acc id planet))
                              {}
                              planets)]
    {:ships-by-player-id ships-by-player-id
     :planets-by-id planets-by-id}))
