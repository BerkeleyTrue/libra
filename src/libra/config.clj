(ns libra.config
  (:require
   [integrant.core :as ig]))

(def config
  {::hotreload? true

   :libra.infra.db/sqlite {:file "data/libra.db"}
   :libra.infra.hotreload/hotreload {:hotreload? (ig/ref ::hotreload?)}
   :libra.infra.http/server {:port 3000
                             :handler (ig/ref :libra.app.router/handler)
                             :on-start-ch (ig/ref :libra.app.core/on-start-ch)}

   :libra.app.core/on-start-ch {}
   :libra.app.components/layout {:hotreload? (ig/ref ::hotreload?)}

   :libra.app.drivers.index/routes {:layout (ig/ref :libra.app.components/layout)}

   :libra.app.router/routes {:index (ig/ref :libra.app.drivers.index/routes)
                             :hotreload (ig/ref :libra.infra.routes/hotreload)}

   :libra.app.router/handler {:routes (ig/ref :libra.app.router/routes)}})
