(ns libra.config
  (:require
    [integrant.core :as ig]))

(def config
  {:dev/hotreload? true


   :infra.routes/hotreload {:hotreload? (ig/ref :dev/hotreload?)}
   :infra.db/sqlite {:file "data/libra.db"}

   :app.router/routes {}})
