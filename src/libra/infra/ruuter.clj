(ns libra.infra.ruuter
  (:refer-clojure :exclude [get post put delete option]))

(defn route [path method handler]
  {:path path
   :method method
   :response handler}) 

(defn get [path handler]
  (route path :get handler))

(defn post [path handler]
  (route path :post handler))

(defn put [path handler]
  (route path :put handler))

(defn delete [path handler]
  (route path :delete handler))

(defn option [path handler]
  (route path :option handler))
