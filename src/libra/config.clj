(ns libra.config
  (:require
   [integrant.core :as ig]))

(def config
  {:dev/hotreload? true

   :infra.db/sqlite {:file "data/libra.db"}
   :infra.routes/hotreload {:hotreload? (ig/ref :dev/hotreload?)}
   :infra/http {:port 3000
                :handler (ig/ref :app.router/handler)
                :on-start-ch (ig/ref :dev/on-start-ch)
                :shutdown-timeout 5000}

   :app.views/layout {:hotreload? (ig/ref :dev/hotreload?)}
   :app.routes/index {:layout (ig/ref :app.views/layout)}

   :app.router/routes {:index (ig/ref :app.routes/index)
                       :hotreload (ig/ref :infra.routes/hotreload)}

   :app.router/handler (ig/ref :app.router/routes)})
