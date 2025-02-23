(ns libra.app.drivers.api.data
  (:require
   [integrant.core :as ig]
   [cheshire.core :as json]
   [libra.infra.ring :as response]
   [libra.utils.dep-macro :refer [defact]]
   [libra.ports.data :as data]))

(defact ->data
  [repo] [_]
  (let [data (data/get-data repo)]
    (-> data
        (json/encode)
        (response/json))))

(defmethod ig/init-key ::routes
  [_ {:keys [repo]}]
  (data/assert-repo repo)
  [{:path "/api/data"
    :method :get
    :response (->data repo)}])
