(ns libra.infra.hotreload
  (:require
   [clojure.core.async :refer [go <!]]
   [integrant.core :as ig]
   [cheshire.core :as json]
   [clojure.java.io :as io]
   [org.httpkit.server :as server]
   [libra.infra.ring :as response]
   [libra.utils.dep-macro :refer [defact]]))

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

(defact ->hotreload
  [on-start-ch]
  [req]
  (let [last-timestamp (last-modified)]
    (server/as-channel
     req
     {:init (fn [ch]
              (server/send!
               ch
               (-> (response/response (->message {:type "connected"
                                                  :last-modified last-timestamp}))
                   (response/content-type "text/event-stream")
                   (response/header "Cache-Control" "no-cache")
                   (response/header "Connection" "keep-alive"))
               false))

      :on-open (fn [ch]
                 (go
                   (loop []
                     (<! on-start-ch)
                     (let [last-modified (last-modified)]
                       (server/send! ch (->message {:type "server-restart"
                                                    :last-modified last-modified})))
                     (recur)))
                 (go
                   (loop []
                     (Thread/sleep 5000)
                     (server/send! ch (->message {:type "loop"
                                                  :last-modified (last-modified)}) false)
                     (recur))))

      :on-message (fn [_ch msg] (println :on-message msg))

      :on-close (fn [_ch status-code] (println :on-close status-code))})))

(defmethod ig/init-key ::routes
  [_ {:keys [on-start-ch]}]

  [{:path "/ping"
    :method :get
    :response (fn [_] (response/response "pong"))}
   {:path "/__hotreload"
    :method :get
    :response (->hotreload on-start-ch)}])
