(ns libra.view.login
  (:require
   [libra.utils.response :as r]
   [libra.view.core :as c]
   [libra.view.layout :as l]
   [libra.database.user :as user]))

(defn logout [req]
  (assoc (r/redirect "/")
         :session nil))

(defn login [req]
  (let [email (get-in req [:params "email"])]
    (if (user/correct-password?
         email
         (get-in req [:params "password"]))
      (assoc (r/redirect "/")
             :session {:user-id (:users/id (user/by-email email))})
      (r/flash-msg (r/redirect "/login") "danger" "Wrong username or password"))))
