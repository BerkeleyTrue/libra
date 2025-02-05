(ns libra.app.drivers.index
  (:require
   [borkdude.html :as h]
   [integrant.core :as ig]
   [libra.infra.ring :as response]
   [libra.utils.dep-macro :refer [defact]]))

(defact ->page
  [layout]
  [req]
  (->> (h/html
        [:div {:class "container"}
         [:h1 "CorpusLibra"]
         [:article
          [:header
           [:p "CorpusLibra is a web application for tracking your weight"]]
          [:section
           [:h2 "Features"]
           [:ul
            [:li "Track your weight"]
            [:li "Track your body fat percentage"]
            [:li "Track your muscle mass"]
            [:li "Track your water weight"]]]]])
       (layout req)
       (response/response)))

(defmethod ig/init-key ::routes
  [_ {:keys [layout]}]
  [{:path "/"
    :method :get
    :response (->page layout)}])
