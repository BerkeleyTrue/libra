(ns libra.view.core
  (:require
   [clojure.java.io :as io]
   [borkdude.html :as h]
   [ring.middleware.anti-forgery :as af]
   [squint.compiler :as squint]
   [libra.infra.html :as hh]))

(defn csrf-token []
  [:input {:type "hidden"
           :name "__anti-forgery-token"
           :value af/*anti-forgery-token*}])

;; Helper functions to interact with the squint compile
(defn ->js [form]
  (->> (squint/compile-string* (str form))
       :body))

(defn compile-jsx [src]
  (squint/compile-string src {:jsx-runtime {:import-source "https://esm.sh/preact@10.19.2"}}))

(comment (compile-jsx "(js/console.log 'hello')"))

(defn cljs-module [filename]
  (let [full-filename (str "cljs/" filename ".cljs")]
    (hh/script
     "module"
     (-> full-filename
         io/resource
         slurp
         compile-jsx
         (str "// corresponding cljs: /static/" full-filename)))))

(comment (cljs-module "htmc"))

(defn cljs-resource [filename]
  (let [full-filename (str "cljs/" filename ".cljs")]
    (h/html
     [:script
      (-> full-filename
          io/resource
          slurp
          ->js
          (str "// corresponding cljs: /static/" full-filename))])))

(defn cljs->inline [filename]
  (-> (str "cljs/" filename ".cljs")
      io/resource
      slurp
      ->js))
