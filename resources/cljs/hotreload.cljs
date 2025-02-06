(def timestamp (atom 0))

(defn log [msg]
  (js/console.log (str "[hotreload] " msg)))

(reset! timestamp (or (js/localStorage.getItem "libra_timestamp") 0))

(add-watch timestamp :watcher (fn [_key _ts _old new-val]
                                (js/localStorage.setItem "libra_timestamp" new-val)))

(defn update-empty-ts [ts]
  (when (zero? @timestamp)
    (reset! timestamp ts)))

(defn should-reload [ts]
  (if (> ts @timestamp)
    (do
      (reset! timestamp ts)
      true)
    false))

(defn init-hot-reload []
  (log (str "running with last timestamp: " @timestamp))
  (if (= (js/typeof js/EventSource) js/undefined)
    (log "EventSource not supported")

    (let [source (js/EventSource. "/__hotreload")]
      (set! (.-onopen source)
            (fn handle-open [_event]
              (log "open")))

      (set! (.-onmessage source)
            (fn handle-message [event]
              (let [{:keys [type last-modified]} (js/JSON.parse (.-data event))]
                (log (str "type: " type " last-modified: " last-modified))
                (update-empty-ts last-modified)
                (when (= type "server-restart")
                  (log "server restart")
                  (when (should-reload last-modified)
                    (.close source)
                    (js/setTimeout #(-> js/window .-location .reload) 500))))))

      (set! (.-onerror source)
            (fn handle-error [event]
              (js/console.log "[hotreload] err" (or (.-message event) event))
              (.close source)
              (js/setTimeout init-hot-reload 1000)))

      (js/addEventListener "beforeunload" #(do (.close source) true)))))

(js/setTimeout init-hot-reload 1000)
