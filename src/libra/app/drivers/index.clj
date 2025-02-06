(ns libra.app.drivers.index
  (:require
   [borkdude.html :as h]
   [integrant.core :as ig]
   [libra.infra.ring :as response]
   [libra.utils.dep-macro :refer [defact]]))

(defact ->page
  [layout]
  [req]
  (->> (h/html
        [:div {:class "bg-pink-400 px-6 py-20 text-center text-surface text-black"}
         [:h1 {:class "mb-6 text-5xl font-bold"}
          [:p "CorpusLibra is a web application for tracking your weight"]]
         [:section
          [:h2 "Features"]
          [:ul
           [:li "Track your weight"]
           [:li "Track your body fat percentage"]
           [:li "Track your muscle mass"]
           [:li "Track your water weight"]]]
         [:a {:class "inline-block rounded bg-primary px-6 pb-2 pt-2.5 text-xs font-medium uppercase leading-normal text-white shadow-primary-3 transition duration-150 ease-in-out hover:bg-primary-accent-300 hover:shadow-primary-2 focus:bg-primary-accent-300 focus:shadow-primary-2 focus:outline-none focus:ring-0 active:bg-primary-600 active:shadow-primary-2 dark:shadow-black/30 dark:hover:shadow-dark-strong dark:focus:shadow-dark-strong dark:active:shadow-dark-strong"
              :href "#!"
              :role "button"}
          "login"]])
       (layout req)
       (response/response)))

(defmethod ig/init-key ::routes
  [_ {:keys [layout]}]
  [{:path "/"
    :method :get
    :response (->page layout)}])
