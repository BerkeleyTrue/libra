(ns libra.utils.error)

(defn stacktrace->str [t]
  (.getMessage t))
