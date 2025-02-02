(ns libra.database.core
  (:require
   [clojure.string :as str]
   [honey.sql :as sql]
   [libra.config :as config]
   [pod.babashka.go-sqlite3 :as jdbc]))

(def db {:db {}})

(defn build-key [key]
  (if (namespace key)
    (str (namespace key) "/" (name key))
    (str (name key))))

(defn to-lower-case-keys [key-map]
  (update-keys key-map #(->
                         (build-key %)
                         str/lower-case
                         keyword)))

(defn execute!
  ([sql]
   (execute! db sql))
  ([tx sql]
   (->> (jdbc/execute! tx (sql/format sql))
        (map to-lower-case-keys))))

(defn execute-one!
  ([sql])
  ([tx sql]))

(defn insert!
  ([table key-map]
   (insert! db table key-map))
  ([tx table key-map]
   (execute-one! tx {:insert-into [table]
                     :values (if (seq? key-map)
                               key-map
                               [key-map])})))

(defn where-eq [key-map]
  (concat [:and]
          (mapv (fn [[k v]] [:= k v]) key-map)))

(defn delete!
  ([table where-params]
   (delete! db table where-params))
  ([tx table where-params]
   (execute! tx {:delete-from [table]
                 :where (where-eq where-params)})))

(defn update!
  ([table key-map where-params]
   (update! db table key-map where-params))
  ([tx table key-map where-params]
   (execute! tx {:update table
                 :set key-map
                 :where (where-eq where-params)})))

(defn find-by-keys
  ([table key-map]
   (find-by-keys db table key-map))
  ([tx table key-map]
   (execute! tx {:select :*
                 :from table
                 :where (where-eq key-map)})))

(defn item-count
  "Helper function to get count for a given table"
  [table]
  (:count (execute-one! {:select [[:%count.*]] :from table})))

(defn paginate
  "Helper function to attach an limit offset paginator to your query"
  ([query page]
   (paginate query page 25))
  ([query page page-size]
   (merge query {:limit page-size :offset (* (- page 1) page-size)})))

(defn paginate-and-search
  "Helper to generate search and paginate query for given table"
  [& {:keys [table where page]}]
  (if where
    (->
     {:select :* :from table
      :where where}
     (paginate page))
    (->
     {:select :* :from table}
     (paginate page))))
