(ns libra.env
  (:require
   [integrant.core :as ig]))

(defmethod ig/init-key ::middleware [_ _]
  [])

(defmethod ig/init-key ::hotreload? [_ _]
  true)
