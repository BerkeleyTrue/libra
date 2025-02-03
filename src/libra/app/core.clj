(ns libra.app.core
  (:require
   [clojure.core.async :refer [chan]]
   [integrant.core :as ig]))

(defmethod ig/init-key ::on-start-ch [_ _]
  (chan 1))
