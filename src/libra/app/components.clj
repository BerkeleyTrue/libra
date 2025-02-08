(ns libra.app.components
  (:require
   [cheshire.core :as json]
   [integrant.core :as ig]
   [borkdude.html :as h]
   [gaka.core :as gaka]
   [libra.infra.html :as hh]
   [libra.utils.embed :refer [js-module]]
   [libra.utils.dep-macro :refer [defact]]))

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

(def ^:dynamic *style*
  (gaka/css
   [:#body
    :width "100dvw"
    :height "100dvh"]))

(defn alert [req]
  (let [msg (get-in req [:flash :message])
        severity (:severity msg)
        msg (:message msg)
        cls {:class (str "alert alert-" severity)}]
    (when msg
      (h/html
       [:div {:role "alert" :& cls}
        msg]))))

(defn link [href body]
  (let [class (str "block text-black/60 "
                   "h-full flex items-center justify-center "
                   "transition duration-200 "
                   "hover:text-black/80 hover:ease-in-out "
                   "focus:text-black/80 active:text-black/80 motion-reduce:transition-none "
                   "text-white/60 hover:text-white/80 focus:text-white/80 active:text-white/80 lg:px-2")]
   (h/html
    [:a {:href href
         :& {:class class}}
     body])))

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
           [:link {:rel "stylesheet" :href "/public/css/style.css"}]
           (global-importmap)
           ; (c/cljs-module "register-sw")
           (when hotreload?
             (js-module "hotreload"))
           [:style *style*]]
          [:body {:id "body" :class "w-dvw h-dvh flex flex-col items-center bg-fuchsia-200 text-black"}
           [:nav {:class "w-full bg-purple-300 h-12 flex justify-between items-center px-8 shadow-md "}
            [:ul {:class "flex justify-between items-center h-12 text-fuchsia-100"}
             [:li {:class "w-full h-full"}
              [:a {:href "/" :class "w-full h-full flex items-center justify-center"}
               [:strong "CorpusLibra"]]]]
            [:ul {:class "flex justify-between items-center h-12"}
             [:li {:class "mr-2 w-full h-full"}
              (link "/" "Home")]
             [:li {:class "mr-2 w-full h-full"}
              (link "/login" "Login")]]]
           ; (hc/htmc)
           (alert req)
           [:div {:class "w-full flex-grow flex flex-col items-center"}
            body]
           [:footer {:class "w-full bg-purple-200 h-12 flex items-center px-8"}
            [:p "© 2021 CorpusLibra"]]]]])

       (str)))

(comment
  ((->layout false) {} "foo"))

(defmethod ig/init-key ::layout
  [_ {:keys [hotreload?]}]
  (->layout hotreload?))
