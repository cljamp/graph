(ns cljamp.graph.utils)

(defn select-keys-exclude
  [m exclude-keys]
  (->> m
       keys
       (filter #(not-any? #{%} exclude-keys))
       (select-keys m)))
