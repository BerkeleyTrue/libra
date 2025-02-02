(ns libra.utils.hotreload
  (:require
   [integrant.core :as ig]
   [clojure.java.io :as io]
   [libra.infra.ring :as response]
   [libra.utils.dep-macro :refer [defact]]))

(defn last-modified []
  (let [lastModified-list (->>
                           (io/resource "cljs")
                           io/file
                           .listFiles
                           (map #(.lastModified %)))]
    (apply max lastModified-list)))

(comment (last-modified))

(defn modified? [last-timestamp]
  (not= (Long. last-timestamp) (last-modified)))

(comment (modified? 0))

(defn has-changes
  ([last-timestamp] (has-changes last-timestamp 0))
  ([last-timestamp loop-count]
   (cond
     (= 30 loop-count)
     (str last-timestamp)

     (modified? last-timestamp)
     (str (last-modified))

     :else
     (do
       (Thread/sleep 200)
       (recur last-modified (inc loop-count))))))

(defact ->hotreload
  [hotreload?]
  [req]
  (let [last-timestamp (get-in req [:query-params "last-modified"])]
    (response/response (if hotreload?
                         (has-changes last-timestamp)
                         "Disabled"))))

(defmethod ig/init-key :infra.routes/hotreload
  [{:keys [hotreload?]} _]

  [{:path "/ping"
    :method :get
    :get #(response/response "pong")}
   {:path "/hotreload"
    :method :get
    :get (->hotreload hotreload?)}])
