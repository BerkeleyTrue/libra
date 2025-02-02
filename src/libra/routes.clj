(ns libra.routes
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

(defmethod ig/init-key :app.router/routes
  [{:keys [hotreload index]} _]
  (into [] (concat hotreload index)))

(defmethod ig/init-key :app.router/handler
  [{:keys [routes]} _]
  (-> (->handler routes)
      (af/wrap-anti-forgery)
      (f/wrap-flash)
      (s/wrap-session)
      (p/wrap-params)))
