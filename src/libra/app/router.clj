(ns libra.app.router
  (:require
   [ruuter.core :as ruuter]
   [integrant.core :as ig]
   [ring.middleware.anti-forgery :as af]
   [ring.middleware.session :as s]
   [ring.middleware.params :as p]
   [ring.middleware.flash :as f]
   [libra.utils.dep-macro :refer [defact]]))

(defact ->handler
  [routes]
  [req]
  (ruuter/route routes req))

(defmethod ig/init-key ::middleware [_ {:keys [env-middleware]}]
  (into [] (concat [af/wrap-anti-forgery
                    f/wrap-flash
                    s/wrap-session
                    p/wrap-params]
                   env-middleware)))

(defmethod ig/init-key ::routes
  [_ {:keys [hotreload index]}]
  (into [] (concat hotreload index)))

(defmethod ig/init-key ::handler
  [_ {:keys [routes middleware]}]
  (reduce #(%2 %1) (->handler routes) middleware))
