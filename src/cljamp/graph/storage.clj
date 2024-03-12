(ns cljamp.graph.storage
  (:require
   [cljamp.graph.action :as action]
   [cljamp.graph.actions.format-str :as format-str]))

(defprotocol NodesStorage

  (get-node
    [external-storage node-name]
    "Extact node from storage by name")

  (get-node-names
    [external-storage]
    "Get all node names"))

(deftype MapStorage
         [nodes-map]

  NodesStorage

  (get-node [_ node-name] (get nodes-map node-name))

  (get-node-names [_] (-> nodes-map keys set)))

(defn ->map-storage
  [nodes-map]
  (->MapStorage nodes-map))

(defn low-level-actions-list->map-storage
  [low-level-actions]
  (->map-storage (reduce #(assoc % (action/node-name %2) %2) {} low-level-actions)))

(def low-level-actions-storage
  (low-level-actions-list->map-storage [format-str/action]))

(deftype UnitedStorage
         [fixed-actions-storage dynamic-storage]

  NodesStorage

  (get-node
    [_ node-name]
    (some (fn [storage]
            (get-node storage node-name))
          [low-level-actions-storage
           fixed-actions-storage
           dynamic-storage]))

  (get-node-names
    [_]
    (-> low-level-actions-storage
        get-node-names
        (concat (get-node-names fixed-actions-storage))
        (concat (get-node-names dynamic-storage))
        set)))

(defn ->united-storage
  ([dynamic-storage] (->united-storage [] dynamic-storage))
  ([fixed-actions dynamic-storage] (->UnitedStorage (low-level-actions-list->map-storage fixed-actions)
                                                    dynamic-storage)))
