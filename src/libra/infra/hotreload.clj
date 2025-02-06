(ns libra.infra.hotreload
  (:require
   [integrant.core :as ig]
   [cheshire.core :as json]
   [clojure.java.io :as io]
   [org.httpkit.server :as server]
   [libra.infra.ring :as response]))

(defn get-directory-timestamps [dir]
  (->> (io/resource dir)
       (io/file)
       (.listFiles)
       (map #(.lastModified %))))

(defn last-modified []
  (let [cljs-ts (get-directory-timestamps "cljs")
        src-ts (get-directory-timestamps "libra")
        ts (concat cljs-ts src-ts)]
    (if (seq ts)
      (apply max ts)
      0)))

(comment (last-modified))

(defn modified? [last-timestamp]
  (not= (Long. last-timestamp) (last-modified)))

(comment (modified? 0))

(defn ->message [data]
  (str "data: " (json/encode data) "\n\n"))

(def channels (atom #{}))

(defn on-open [ch]
  (swap! channels conj ch))

(defn hotreload [req]
  (let [last-timestamp (last-modified)]
    (server/as-channel
     req
     {:init (fn [ch]
              (server/send!
               ch
               (-> (response/response (->message {:message "connected"
                                                  :last-modified last-timestamp}))
                   (response/content-type "text/event-stream")
                   (response/header "Cache-Control" "no-cache")
                   (response/header "Connection" "keep-alive"))
               false))
      :on-open on-open
      :on-message (fn [_ch msg] (println :on-message msg))
      :on-close (fn [ch status-code]
                  (println :on-close status-code)
                  (swap! channels #(disj % ch)))})))

(defmethod ig/init-key ::routes
  [_ _]

  [{:path "/ping"
    :method :get
    :response (fn [_] (response/response "pong"))}
   {:path "/__hotreload"
    :method :get
    :response hotreload}])
