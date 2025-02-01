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

(defn route [path method response-fn]
  {:path path
   :method method
   :response (fn [req]
               (let [resp (response-fn req)]
                 (log/info "Response: " resp)
                 (if (string? resp)
                   {:status 200
                    :body resp}
                   resp)))})

(defn get [path response-fn]
  (route path :get response-fn))

(defn post [path response-fn]
  (route path :post response-fn))

(defn put [path response-fn]
  (route path :put response-fn))

(defn delete [path response-fn]
  (route path :delete response-fn))

(defn option [path response-fn]
  (route path :option response-fn))

(defn restricted [response-fn]
  (fn [req]
    (if (session/current-user req)
      (response-fn req)
      (r/redirect (str "/login?url=" (:uri req))))))

;;
;; Extend your routes in here!!!
;;
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
