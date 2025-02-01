(ns libra.view.index
  (:require
   [borkdude.html :as h]
   [libra.view.layout :as l]
   [libra.database.user :as user]))

(defn page [req]
  (l/layout
   req
   (h/html
    [:div {:class "container"}
     [:h1 "My Service"]
     [:table
      [:tr [:td "User"]
       [:td "No user"]]]])))

(defn render-page [req]
  (str (page req)))

(comment (str (page {})))
