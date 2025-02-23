(ns libra.ports.data)

(defprotocol Repository
  (get-data [this])
  (add-data [this item])
  (last-data [this]))

(defn repo? [repo]
  (satisfies? Repository repo))

(defn assert-repo [repo]
  (assert (repo? repo) 
          (str "expected to find a record that satisfies data repository but found " repo)))
