(ns libra.utils.embed
  (:require
   [babashka.fs :as fs]
   [libra.infra.html :refer [script]]))

(defn js-module [filename]
  (let [full-filename (str "public/js/" filename ".mjs")]
    (script {:type "module"}
            (-> full-filename
                (fs/file)
                (slurp)
                (str "\n// corresponding js: /public/cljs/" full-filename "\n")))))

(comment (js-module "htmc"))
