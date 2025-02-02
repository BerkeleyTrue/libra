(ns libra.core
  (:require
   [integrant.core :as ig]
   [taoensso.timbre :as log]
   [taoensso.timbre.appenders.core :as appenders]
   [libra.config :refer [config]]))

(log/merge-config!
 {:appenders {:spit (appenders/spit-appender {:fname "./application.log"})}})

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (log/error
      {:what :uncaught-exception
       :exception ex
       :where (str "Uncaught exception on" (.getName thread))}))))

(defonce system (atom nil))

(defn stop-app []
  (some-> (deref system)
          (ig/halt!))
  (shutdown-agents))

(defn start-app [& _]
  (->> config
       (ig/init)
       (reset! system))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& _]
  (start-app))
