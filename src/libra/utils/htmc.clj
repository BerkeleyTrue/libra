; (ns libra.utils.htmc
;   (:require
;    [borkdude.html :as h]
;    [libra.view.core :as c]))
;
; ;; kudos to https://kalabasa.github.io/htmz/
; (defn htmc "Has to be a function due to the hotreload of the htmc.cljs code. If you want to extend it."
;   [] (h/html
;       [:<>
;        (c/cljs-module "htmc")
;        [:iframe {:id "htmc" :hidden true :name "htmc"}]]))
;
; (comment (htmc))
