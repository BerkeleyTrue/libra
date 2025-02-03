; (ns libra.view.register
;   (:require
;    [libra.database.user :as user]
;    [libra.utils.response :as r]))
;
; (defn save-user [req]
;   (user/insert
;    {:email (get-in req [:params "email"])
;     :password (get-in req [:params "password1"])})
;   (r/redirect "/"))
;
; (defn save [req]
;   (if (= (get-in req [:params "password1"])
;          (get-in req [:params "password2"]))
;     (save-user req)
;     (r/flash-msg
;      (r/redirect "/register")
;      "danger" "Password don't match")))
