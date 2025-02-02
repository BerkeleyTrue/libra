(ns libra.routes
  (:require
   [taoensso.timbre :as log]
   [ruuter.core :as ruuter]
   [libra.utils.hotreload :as hotreload]
   [libra.utils.session :as session]
   [libra.utils.response :as r]
   [libra.view.pwa :as pwa]
   [libra.view.index :as index]
   [libra.view.login :as login]
   [libra.view.profile :as profile]
   [libra.view.register :as register]
   [libra.static :as static]))

(defn handler [req]
  (ruuter/route 
    [(get "/" index/render-page) 
     (get "/manifest.json" pwa/manifest)
     (get "/sw.js" pwa/sw)
     (get "/hotreload" hotreload/hotreload)]
    req))
     ; (get "/static/:filename" static/serve-static)
      ; (get "/register" register/index)
      ; (post "/register" register/save)
      ; (get "/login" login/index)
      ; (post "/login" login/login)
      ; (get "/logout" login/logout)
      ; (get "/profile" profile/index)
