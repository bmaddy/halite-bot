(ns my-bot
  (:require [hlt.entity :as e]
            [hlt.navigation :as nav]
            [hlt.bot :as bot]))

(defn compute-move
  [ship state]
  (when (= (-> ship :docking :status) :undocked)
    (first
     (for [planet (-> state :planets-by-id vals)
           :when (-> planet :owner-id nil?)]
       (if (e/within-docking-range? ship planet)
         (e/dock-move ship planet)
         (nav/navigate-to-dock ship planet state))))))

(def bot
  (reify bot/IBot
    (get-name [this {:keys [player-id] :as state}] (str "basic-bot-" player-id))
    (next-moves [this {:keys [player-id] :as state}]
      (let [ships (get-in state [:ships-by-player-id player-id])]
        (keep #(compute-move % state) ships)))))

