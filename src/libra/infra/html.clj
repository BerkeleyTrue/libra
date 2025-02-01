(ns libra.infra.html
  (:require
   [borkdude.html :as h]))

(defn script [type content]
  (h/html
   [:$
    (str
     "<script type=" type ">"
     content
     "</script>")]))
