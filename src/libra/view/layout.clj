(ns libra.view.layout
  (:require
   [cheshire.core :as json]
   [borkdude.html :as h]
   [libra.config :refer [hotreload?]]
   [libra.infra.html :as hh]
   [libra.utils.session :as s]
   [libra.utils.response :as r]
   [libra.utils.htmc :as hc]
   [libra.view.style :as sty]
   [libra.view.core :as c]))

(def squint-cdn-path "https://cdn.jsdelivr.net/npm/squint-cljs@0.8.114")

(defn paginator [req current-page pages base-url]
  (let* [q (:query-params req)
         next (when (not= current-page pages)
                (str base-url "?"
                     (r/query-params->url
                      (merge q {"page" (+ current-page 1)}))))
         previous (when (not= current-page 1)
                    (str base-url "?"
                         (r/query-params->url
                          (merge q {"page" (- current-page 1)}))))]
        [:div {:class "d-flex justify-content-center mb-2"}
         [:div {:class "btn-group"}
          [:a {:class "btn btn-primary"}
           (if (nil? previous)
             {:disabled true}
             {:href previous}) "Previous"]
          [:a {:class "btn btn-outline-primary" :href "#"} current-page " / " pages]
          [:a {:class "btn btn-primary"}
           (if (nil? next)
             {:disabled true}
             {:href next}) "Next"]]]))

(defn autocomplete-input [& {:keys [label name value list required]}]
  [:div {:class "mb-3"}
   [:label {:class "form-label"} label]
   [:input {:class "form-control" :type "input" :list (str name "list")
            :name name :value value :required required
            :autocomplete "off"}]
   [:datalist {:id (str name "list")}
    (map (fn [e] [:option {:value e}]) list)]])

(defn form-input [& {:keys [label type name value required id]
                     :as opts
                     :or {required false}}]
  (cond
    (= type "textarea")
    [:div {:class "mb-3"}
     [:label {:class "form-label"} label]
     [:textarea {:class "form-control" :type type :name name :required required} value]]

    (= type "autocomplete")
    (autocomplete-input opts)

    (= type "base64-upload")
    [:div {:class "mb-3"}
     [:label {:class "form-label"} label]
     (c/cljs-module "base64-upload")
     [:input {:class "form-control" :type "file" :required required :onchange (str "base64_upload(\"" id "\", this)")}]
     [:input {:type "hidden" :name name :id (if id id label)}]]

    :else
    [:div.mb-3
     [:label.form-label label]
     [:input.form-control {:type type :value value :name name :required required}]]))

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

(defn navbar [req]
  (let [user (s/current-user req)]
    (h/html
     [:nav {:class "navbar sticky-top navbar-expand-lg navbar-bg-body-tertiary"}
      [:div {:class "container-fluid"}
       [:a {:class "navbar-brand fw-bold" :href "/"} "bork·web"]
       [:button {:class "navbar-toggler" :type "button" :data-bs-toggle "collapse" :data-bs-target "#navbar"}
        [:span {:class "navbar-toggler-icon"}]]
       [:div {:id "navbar" :class "collapse navbar-collapse"}
        (when (not user)
          (h/html
           [:ul {:class "navbar-nav"}
            [:li {:class "nav-item"}
             [:a {:class "nav-link" :href "/login"} "Login"]]
            [:li {:class "nav-item"}
             [:a {:class "nav-link" :href "/register"} "Register"]]
            [:li {:class "nav-item"}
             [:a {:class "nav-link" :href "/kitchensink"} "Kitchensink"]]]))
        (when user
          (h/html
           [:ul {:class "navbar-nav"}
            [:li {:class "nav-item"}
             [:a {:class "nav-link" :href "/profile"} "Profile"]]
            [:li {:class "nav-item"}
             [:a {:class "nav-link" :href "/logout"} "Logout"]]]))]]])))

(defn alert [req]
  (let [msg (get-in req [:flash :message])
        severity (:severity msg)
        msg (:message msg)
        cls {:class (str "alert alert-" severity)}]
    (when msg
      (h/html
       [:div {:role "alert" :& cls}
        msg]))))

(defn layout [req & body]
  (h/html
    [:<>
     [:$ "<!DOCTYPE html>"]
     [:html
      [:head
       [:meta {:charset "utf-8"}]
       [:meta {:name "viewport"
               :content "width=device-width, initial-scale=1"}]
       [:link {:rel "manifest" :href "/manifest.json"}]
       (global-importmap)
       (c/cljs-module "register-sw")
       (when hotreload?
         (c/cljs-module "hotreload"))
       [:style sty/*style*]]
      [:body {:data-bs-theme "dark" :id "body"}
       (hc/htmc)
       (navbar req)
       (alert req)
       body]]]))

(defn modal [& {:keys [id title content actions]}]
  [:div {:class "modal fade" :tabindex -1 :id id}
   [:div {:class "modal-dialog"}
    [:div {:class "modal-content"}
     [:div {:class "modal-header"}
      [:h5 {:class "modal-title"} title]
      [:button {:class "btn-close" :type "button" :data-bs-dismiss "modal"}]]
     [:div {:class "modal-body"} content]
     [:div {:class "modal-footer"} actions]]]])
