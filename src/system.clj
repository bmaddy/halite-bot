(ns system
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            ;; [clojure.tools.logging :as log]
            ;; [hlt.log :as log]
            [taoensso.timbre :as timbre :refer [info]]
            [taoensso.timbre.appenders.core :as appenders])
  (:gen-class))

;; ;; Turn on file logging
;; (log/set-config! [:appenders :spit :enabled?] true)
;; ;; Set the log file location
;; (log/set-config! [:shared-appender-config :spit-filename] "out.log")
(timbre/merge-config!
 {:appenders {:spit (appenders/spit-appender {:fname "out.log"})
              :println {:enabled? false}}})

(defn start
  [bot]
  (info {:bot-name :FIXME
         :player-id :player-id
         :started-at (java.util.Date.)
         :base-dirname "log"})
  (info :starting)
  (info (read *in*))
  (info :done-1)
  (info (read *in*))
  (info :done-2)
  (info (read *in*))
  (info :done-3)
  (info (read *in*))
  (info :done-4)
  (info (read *in*))
  (info :done-5)
  (info (read *in*))
  (info :done-6)
  (info (read *in*))
  (info :done-7)
  (info (read *in*))
  (info :done-8)
  (info (read *in*))
  (info :done-9)
  (info (read *in*))
  (info :done-10)
  (info (read *in*))
  (info :done-11)
  (info (read *in*))
  (info :done-12)
  (info (read *in*))
  (info :done-13)
  (info (read *in*))
  (info :done-14)
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
