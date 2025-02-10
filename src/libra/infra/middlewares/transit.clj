(ns libra.infra.middlewares.transit
  (:import (java.io ByteArrayOutputStream))
  (:require [libra.infra.ring :as response]
            [libra.utils.keywordize-map :refer [keywordize-map]]
            [cognitect.transit :as transit]))

(defn- write [x]
  (let [baos (ByteArrayOutputStream.)
        w    (transit/writer baos :json)
        _    (transit/write w x)
        ret  (.toString baos "UTF-8")]
    (.reset baos)
    ret))

(defn- transit-response? [response]
  (= (response/get-header response "Content-Type") 
     "application/transit+json"))

(defn- transit-request? [request]
  (= (response/get-header request "Content-Type")
     "application/transit+json"))

(defn- read-transit [request]
  (when (transit-request? request)
    (when-let [body (:body request)]
      (let [rdr (transit/reader body :json)]
        (try
          [true (keywordize-map (transit/read rdr))]
          (catch Exception ex
            [false ex]))))))

(def malformed-response
  (-> (response/response "Malformed Transit in request body.")
      (response/status 400)
      (response/content-type "text/plain")))

(defn- transit-handler
  [handler f sentinel-key]
  (fn [request]
    (if (get request sentinel-key)
      (handler request)
      (if-let [[ok? res] (read-transit request)]
        (if ok?
          (handler (assoc (f request res) sentinel-key true))
          (do
            (println "Malformed Transit in request body.")
            (println res)
            malformed-response))
        (handler request)))))

(defn wrap-transit-body
  "Middleware that parses the body of Transit request maps, and replaces the :body
  key with the parsed data structure. Requests without a Transit content type are
  unaffected."
  [handler]
  (letfn [(assoc-body [req data] (assoc req :body data))]
    (transit-handler handler assoc-body ::wrap-transit-body)))

(defn wrap-transit-params
  "Middleware that parses the body of Transit requests into a map of parameters,
  which are added to the request map on the :params keys.

  Use the standard Ring middleware, ring.middleware.keyword-params, to
  convert the parameters into keywords."
  [handler]
  (letfn [(assoc-transit-params [req data]
            (if (map? data)
              (update-in req [:params] merge data)
              req))]
    (transit-handler handler assoc-transit-params ::wrap-transit-params)))

(defn transit-response
  "Create a transit response based on provided options. Will leave responses
  with non-transit Content Type will be returned unaltered."
  [response]
  (if (and (coll? (:body response))
           (contains? (:headers response) "Content-Type")
           (transit-response? response))
    (update-in response [:body] write)
    response))

(defn wrap-transit-response
  "Middleware that converts responses with a map or a vector for a body into a
  Transit response."
  [handler]
  (fn transit-handler [request]
    (-> (handler request)
        (transit-response))))

(defn wrap-transit [handler]
  (-> handler
      wrap-transit-body
      wrap-transit-params
      wrap-transit-response))
