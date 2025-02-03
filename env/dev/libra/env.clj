(ns libra.env
  (:require
   [integrant.core :as ig]
   [ring.middleware.reload :as reload]
   [ring.middleware.stacktrace :as stacktrace]))

(defmethod ig/init-key ::middleware [_ _]
  [reload/wrap-reload stacktrace/wrap-stacktrace])
