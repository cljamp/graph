(ns cljamp.graph.storage
  (:require
   [cljamp.graph.base-rich-fns.format-str :as format-str]
   [cljamp.graph.base-rich-fns.map :as map-fn]))

(defprotocol Protocol

  (name->graph
    [external-storage graph-name]
    "Extract node from storage by name")

  (graph-names
    [external-storage]
    "Get all node names"))

(deftype MapStorage
  [nodes-map]

  Protocol

  (name->graph [_ graph-name] (get nodes-map graph-name))


  (graph-names [_] (-> nodes-map keys set)))

(defn ->map-storage
  [nodes-map]
  (->MapStorage nodes-map))

(defn base-rich-fns-list->map-storage
  [base-rich-fns]
  (->map-storage (reduce #(assoc % (:graph-name %2) %2) {} base-rich-fns)))

(def base-rich-fns-storage
  (base-rich-fns-list->map-storage [format-str/rich-fn
                                        map-fn/rich-fn]))

(deftype UnitedStorage
  [fixed-rich-fns-storage dynamic-storage]

  Protocol

  (name->graph
    [_ graph-name]
    (some (fn [storage]
            (name->graph storage graph-name))
          [base-rich-fns-storage
           fixed-rich-fns-storage
           dynamic-storage]))


  (graph-names
    [_]
    (-> base-rich-fns-storage
        graph-names
        (concat (graph-names fixed-rich-fns-storage))
        (concat (graph-names dynamic-storage))
        set)))

(defn ->united-storage
  ([dynamic-storage] (->united-storage [] dynamic-storage))
  ([fixed-rich-fns dynamic-storage] (->UnitedStorage (base-rich-fns-list->map-storage fixed-rich-fns)
                                                    dynamic-storage)))
