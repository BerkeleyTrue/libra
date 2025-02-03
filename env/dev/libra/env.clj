(ns libra.env
  (:require
   [integrant.core :as ig]))

(defmethod ig/init-key ::middleware [_ _]
  [])
