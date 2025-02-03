(ns libra.app.components
  (:require
   [cheshire.core :as json]
   [borkdude.html :as h]
   [integrant.core :as ig]
   [libra.infra.html :as hh]
   [libra.utils.dep-macro :refer [defact]]
   ; [libra.utils.session :as s]
   ; [libra.utils.htmc :as hc]
   [libra.view.style :as sty]))
   ; [libra.view.core :as c]))

(def squint-cdn-path "https://cdn.jsdelivr.net/npm/squint-cljs@0.8.114")

(defn global-importmap []
  (hh/script
   "importmap"
   (json/encode
    {:imports
     {:squint-cljs/core.js (str squint-cdn-path "/src/squint/core.js")
      :squint-cljs/string.js (str squint-cdn-path "/src/squint/string.js")
      "squint-cljs/src/squint/string.js" (str squint-cdn-path "/src/squint/string.js")
      "squint-cljs/src/squint/set.js" (str squint-cdn-path "/src/squint/set.js")
      "squint-cljs/src/squint/html.js" (str squint-cdn-path "/src/squint/html.js")}}
    {:pretty true})))

(defn alert [req]
  (let [msg (get-in req [:flash :message])
        severity (:severity msg)
        msg (:message msg)
        cls {:class (str "alert alert-" severity)}]
    (when msg
      (h/html
       [:div {:role "alert" :& cls}
        msg]))))

(defact ->layout
  [hotreload?]
  [req & body]
  (->> (h/html
        [:<>
         [:$ "<!DOCTYPE html>"]
         [:html
          [:head
           [:meta {:charset "utf-8"}]
           [:meta {:name "viewport"
                   :content "width=device-width, initial-scale=1"}]
           [:link {:rel "manifest" :href "/manifest.json"}]
           (global-importmap)
           ; (c/cljs-module "register-sw")
           ; (when hotreload?
           ;   (c/cljs-module "hotreload"))
           [:style sty/*style*]]
          [:body {:data-bs-theme "dark" :id "body"}
           ; (hc/htmc)
           (alert req)
           body]]])
       (str)))

(comment
  ((->layout false) {} "foo"))

(defmethod ig/init-key ::layout
  [_ {:keys [hotreload?]}]
  (->layout hotreload?))
