(ns libra.app.drivers.api.data
  (:require
   [integrant.core :as ig]
   [cheshire.core :as json]
   [libra.infra.ring :as response]
   [libra.utils.dep-macro :refer [defact]]))

(defact ->data
  []
  [_]
  (let [data [{:date "2024-01-01" :weight 256}
              {:date "2024-01-04" :weight 254}
              {:date "2024-01-07" :weight 253}
              {:date "2024-01-10" :weight 251}
              {:date "2024-01-13" :weight 249}
              {:date "2024-01-16" :weight 248}
              {:date "2024-01-19" :weight 245}
              {:date "2024-01-22" :weight 243}
              {:date "2024-01-25" :weight 240}
              {:date "2024-01-28" :weight 238}
              {:date "2024-01-31" :weight 235}
              {:date "2024-02-03" :weight 233}
              {:date "2024-02-06" :weight 231}
              {:date "2024-02-09" :weight 229}
              {:date "2024-02-12" :weight 227}
              {:date "2024-02-15" :weight 225}
              {:date "2024-02-18" :weight 223}
              {:date "2024-02-21" :weight 222}
              {:date "2024-02-24" :weight 221}
              {:date "2024-02-27" :weight 220}]]
    (-> data
        (json/encode) 
        (response/json))))

(defmethod ig/init-key ::routes
  [_ _]
  [{:path "/api/data"
    :method :get
    :response (->data)}])
