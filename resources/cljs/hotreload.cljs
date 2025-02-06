(def timestamp (atom 0))

(defn init-hot-reload []
  (js/console.log "Hotreload running")
  (if (= (js/typeof js/EventSource) js/undefined)
    (js/console.log "EventSource not supported")

    (let [source (js/EventSource. "/__hotreload")]
      (set! (.-onopen source)
            (fn handle-open [_event]
              (js/console.log "hotreload: open")))

      (set! (.-onmessage source)
            (fn handle-message [event]
              (let [data (js/JSON.parse (.-data event))]
                (js/console.log "hotreload: message" data))))
                ; (case (.-data event)
                ;   "updated" (do (js/console.log "hotreload: updated")
                ;                 (.close source)
                ;                 (js/setTimeout #(-> js/window .-location .reload) 500))
                ;   "connected" (js/console.log "hotreload: connected")
                ;   (js/console.log "hotreload: unknown event" event)))))

      (set! (.-onerror source) 
            (fn handle-error [event]
              (js/console.log "hotreload: err" (or (.-message event) event))
              (.close source)
              (js/setTimeout init-hot-reload 1000)))

      (js/addEventListener "beforeunload" #(do (.close source) true)))))

(js/setTimeout init-hot-reload 1000)
