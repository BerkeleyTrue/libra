(ns libra.view.profile
  (:require
   [libra.view.layout :as l]))

(defn index [req]
  (l/layout
   req
   [:h1 "Profile"]))
