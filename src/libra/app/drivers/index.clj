(ns libra.app.drivers.index
  (:require
   [borkdude.html :as h]
   [integrant.core :as ig]
   [libra.infra.ring :as response]
   [libra.utils.dep-macro :refer [defact]]
   [libra.utils.embed :refer [js-module]]))

(defact ->page
  [layout]
  [req]

  (->> [:div {:class "container"}
        [:canvas {:id "libra"}]
        (js-module "libra-chart")]
       (h/html)
       (layout req)
       (response/response)))

(defmethod ig/init-key ::routes
  [_ {:keys [layout]}]
  [{:path "/"
    :method :get
    :response (->page layout)}])
