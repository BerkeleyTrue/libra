(ns libra.core
  (:require
   [ring.middleware.anti-forgery :as af]
   [ring.middleware.session :as s]
   [ring.middleware.params :as p]
   [ring.middleware.flash :as f]
   [taoensso.timbre :as log]
   [taoensso.timbre.appenders.core :as appenders]
   [org.httpkit.server :as srv]
   [libra.routes :as ro]))

(def server (atom nil))

(log/merge-config!
 {:appenders {:spit (appenders/spit-appender {:fname "./application.log"})}})

(defn start-server [port]
  (log/info "Server starting up!")
  (reset! server
          (srv/run-server
           (->
            #'ro/routes
            (af/wrap-anti-forgery {:anti-forgery true
                                   :token-expiry (* 60 60 24)})
            f/wrap-flash
            s/wrap-session
            p/wrap-params)
           {:port port
            :join? false})))
;;
;; Repl functions. To startup and stop the system
;;
(comment (start-server 8080))
(comment (@server))
