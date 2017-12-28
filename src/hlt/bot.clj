(ns hlt.bot
  (:refer-clojure :exclude [name]))

(defprotocol IBot
  (get-name [this state])
  (next-moves [this state]))
