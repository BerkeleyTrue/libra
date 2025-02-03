(ns user
  (:require
   [integrant.core :as ig]
   [integrant.repl :as ig-repl :refer [halt reset]]
   ; [portal.api :as p]
   [libra.config :refer [config]]))

(ig/load-namespaces config)

(ig-repl/set-prep! #(config))

(defn run-config [key deps f]
  (let [system (ig/init config (conj deps key))
        dep (get system key)]
    (f dep)
    (ig/halt! system)))

(defn go []
  (ig-repl/go))

(comment
  ()
  (ig-repl/go) ; starts the system
  (halt) ; stops the system
  (reset) ; resets the system
  ; (do) 
    ; (add-tap #'p/submit)
    ; (p/open)
    ; (tap> :set))
  ,)
