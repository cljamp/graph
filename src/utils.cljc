(ns utils)

(defn select-keys-exclude
  [m exclude-keys]
  (->> m
       keys
       (filter #(not-any? #{%} exclude-keys))
       (select-keys m)))

(defn ->action-name
  [_get-common-action-fn action-name & _args]
  action-name)
