(ns libra.core
  (:require
   [ring.middleware.anti-forgery :as af]
   [ring.middleware.session :as s]
   [ring.middleware.params :as p]
   [ring.middleware.flash :as f]
   [taoensso.timbre :as log]
   [taoensso.timbre.appenders.core :as appenders]
   [org.httpkit.server :as httpkit]
   [libra.routes :as ro]))

(defonce server (atom nil))

(log/merge-config!
 {:appenders {:spit (appenders/spit-appender {:fname "./application.log"})}})

(defn start-server [port]
  (log/info "Server starting up!")
  (try
    (let [final-handler (-> ro/handler
                            (af/wrap-anti-forgery)
                            (s/wrap-session)
                            (p/wrap-params)
                            (f/wrap-flash))
          srvr (httpkit/run-server final-handler {:port port :legacy-return-value? false})]
      (reset! server srvr)
      srvr)
    (catch Throwable e
      (log/warn "Error starting server" e)
      (throw e))))

(defn stop-server []
  (when-let [srvr @server]
    (log/info "Server shutting down!")
    (when @(future (httpkit/server-stop! srvr))
      (log/info "Server stopped")
      (reset! server nil))))

(defn restart-server [port]
  (stop-server)
  (start-server port))
;;
;; Repl functions. To startup and stop the system
;;
(comment (restart-server 3000))
(comment (@server))
