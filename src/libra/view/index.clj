(ns libra.view.index
  (:require
   [borkdude.html :as h]
   [integrant.core :as ig]
   [libra.utils.dep-macro :refer [defact]]
   [libra.view.layout]))

(defact ->page
  [layout]
  [req]
  (layout
   req
   (str
    (h/html
     [:div {:class "container"}
      [:h1 "My Service"]
      [:table
       [:tr [:td "User"]
        [:td "No user"]]]]))))

(defmethod ig/init-key :app.routes/index
  [{:keys [layout]} _]
  [{:path "/"
    :method :get
    :get (->page layout)}])
