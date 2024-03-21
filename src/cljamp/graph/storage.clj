(ns cljamp.graph.storage
  (:require
   [cljamp.graph.action :as action]
   [cljamp.graph.actions.format-str :as format-str]
   [cljamp.graph.actions.map :as map-action]))

(defprotocol Protocol

  (name->graph
    [external-storage graph-name]
    "Extact node from storage by name")

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

(defn low-level-actions-list->map-storage
  [low-level-actions]
  (->map-storage (reduce #(assoc % (action/graph-name %2) %2) {} low-level-actions)))

(def low-level-actions-storage
  (low-level-actions-list->map-storage [format-str/action
                                        map-action/action]))

(deftype UnitedStorage
  [fixed-actions-storage dynamic-storage]

  Protocol

  (name->graph
    [_ graph-name]
    (some (fn [storage]
            (name->graph storage graph-name))
          [low-level-actions-storage
           fixed-actions-storage
           dynamic-storage]))


  (graph-names
    [_]
    (-> low-level-actions-storage
        graph-names
        (concat (graph-names fixed-actions-storage))
        (concat (graph-names dynamic-storage))
        set)))

(defn ->united-storage
  ([dynamic-storage] (->united-storage [] dynamic-storage))
  ([fixed-actions dynamic-storage] (->UnitedStorage (low-level-actions-list->map-storage fixed-actions)
                                                    dynamic-storage)))
