(ns libra.app.router
  (:require [taoensso.timbre :as log]
            [ruuter.core :as ruuter]
            [integrant.core :as ig]
            [ring.middleware.anti-forgery :as af]
            [ring.middleware.session :as s]
            [ring.middleware.params :as p]
            [ring.middleware.flash :as f]
            [libra.utils.dep-macro :refer [defact]]
            [libra.infra.middlewares.logger :as logger]))

(defact ->handler
  [routes]
  [req]
  (ruuter/route routes req))

(defmethod ig/init-key ::middleware [_ {:keys [env-middleware]}]
  (into [] (concat [af/wrap-anti-forgery
                    f/wrap-flash
                    s/wrap-session
                    p/wrap-params]
                   env-middleware
                   [logger/logger])))

(defmethod ig/init-key ::routes
  [_ {:keys [hotreload index static]}]
  (into [] (concat hotreload index static)))

(defmethod ig/init-key ::handler
  [_ {:keys [routes middlewares]}]
  (log/info "Initializing handler" routes)
  (reduce #(%2 %1) (->handler routes) middlewares))
