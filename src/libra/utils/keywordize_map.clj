(ns libra.utils.keywordize-map
  (:require [clojure.walk :as walk]))

(defn keywordize-map
  "Recursively transforms all map string keys into keywords."
  [m]
  (let [f (fn [[k v]]
            (if (string? k)
              [(keyword k) v]
              [k v]))]
    (walk/postwalk
     (fn [x]
       (if (map? x)
         (into {} (map f x))
         x))
     m)))

(comment
  (keywordize-map {"foo" "bar" "baz" {"qux" "quux"}})
  ,);; => {:foo "bar", :baz {:qux "quux"}}
  
