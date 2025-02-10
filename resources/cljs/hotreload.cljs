(def clj-ts (atom 0))
(def cljs-ts (atom 0))

(def debug? false)

(defn log [& args]
  (when debug?
    (.apply (.-log js/console) js/console (into-array (cons "[hotreload]" args)))))

(reset! clj-ts (or (js/localStorage.getItem "libra.clj-ts") 0))
(reset! cljs-ts (or (js/localStorage.getItem "libra.cljs-ts") 0))

(add-watch clj-ts :watcher-clj
           (fn [_key _ts _old new-val]
             (js/localStorage.setItem "libra.clj-ts" new-val)))

(add-watch cljs-ts :watcher-cljs
           (fn [_key _ts _old new-val]
             (js/localStorage.setItem "libra.cljs-ts" new-val)))

(defn update-empty-ts [{:keys [cljs clj]}]
  (when (zero? @clj-ts)
    (reset! clj-ts clj))
  (when (zero? @cljs-ts)
    (reset! cljs-ts cljs)))

(defn should-reload-clj [{:keys [clj]}]
  (if (> clj @clj-ts)
    (do
      (reset! clj-ts clj)
      true)
    false))

(defn should-reload-cljs [{:keys [cljs]}]
  (if (> cljs @cljs-ts)
    (do
      (reset! cljs-ts cljs)
      true)
    false))

(defn init-hot-reload []
  (log "running with last timestamp: " @clj-ts @cljs-ts)
  (if (= (js/typeof js/EventSource) js/undefined)
    (log "EventSource not supported")

    (let [source (js/EventSource. "/__hotreload")]
      (set! (.-onopen source)
            (fn handle-open [_event]
              (log "open")))

      (set! (.-onmessage source)
            (fn handle-message [event]
              (let [{:keys [type data]} (js/JSON.parse (.-data event))]
                (log "type: " type " last-modified: " data)
                (update-empty-ts data)
                (cond
                  (= type "server-restart")
                  (do
                    (log "server restart")
                    (when (should-reload-clj data)
                      (.close source)
                      (js/setTimeout #(-> js/window .-location .reload) 500)))

                  (= type "loop")
                  (do
                    (log "loop")
                    (when (should-reload-cljs data)
                      (.close source)
                      (js/setTimeout #(-> js/window .-location .reload) 500)))))))

      (set! (.-onerror source)
            (fn handle-error [event]
              (js/console.log "[hotreload] err" (or (.-message event) event))
              (.close source)
              (js/setTimeout init-hot-reload 1000)))

      (js/addEventListener "beforeunload" #(do (.close source) true)))))

(js/setTimeout init-hot-reload 1000)
