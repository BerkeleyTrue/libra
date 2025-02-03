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
         [:h1 "My Service"]
         [:table
          [:tr [:td "User"]
           [:td "No user"]]]])
       (layout req)
       (response/response)))

(defmethod ig/init-key ::routes
  [_ {:keys [layout]}]
  [{:path "/"
    :method :get
    :response (->page layout)}]) 
