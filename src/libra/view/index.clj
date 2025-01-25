(ns libra.view.index
  (:require
   [libra.infra.hiccup :as h]
   [libra.view.layout :as l]
   [libra.database.user :as user]))

(h/defhtml page [req]
  [:div.container
   [:h1 "My Service"]
   [:table
    [:tr [:td "User"]
     [:td "No user"]]]])

(defn render-page [req]
  (str (page req)))

(comment (str (page {})))
