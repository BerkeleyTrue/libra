(ns libra.infra.http
  (:require
   [clojure.core.async :refer [put!]]
   [integrant.core :as ig]
   [taoensso.timbre :as log]
   [org.httpkit.server :as http]))


(defmethod ig/init-key :infra/http [_ {:keys [handler port on-start-ch]}]
  (log/info "Starting HTTP server on port" port)
  (let [s (http/run-server handler {:port port
                                    :legacy-return-value? false})]
    (log/info "HTTP server started")
    (put! on-start-ch "started")
    s))

(defmethod ig/halt-key! :infra/http [_ server]
  (log/info "Stopping HTTP server")
  (when @(future (http/server-stop! server))
    (log/info "HTTP server stopped")))
