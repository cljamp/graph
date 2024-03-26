(ns cljamp.graph.traversal
  (:require
   [cljamp.graph.storage :as storage]
   [cljamp.graph.rich-fn :as rich-fn]))

(declare storage-graph-name->rich-fn)

(defn extend-arg
  [storage graph arg]
  (if (vector? arg)
    (mapv (partial extend-arg storage graph) arg)
    (if-let [node (get graph arg)]
      (if (vector? node)
        (storage-graph-name->rich-fn storage arg)
        arg)
      arg)))

(defn storage-graph->rich-fn
  [storage {{[template-graph-name template-args] :return
             :as graph} :graph}]
  (reduce rich-fn/carry
          (storage-graph-name->rich-fn storage
                                      template-graph-name)
          (map (fn [[arg-name arg-value]] [arg-name (extend-arg storage graph arg-value)])
               template-args)))

(defn storage-graph-name->rich-fn
  [storage graph-name]
  (let [graph (storage/name->graph storage graph-name)]
    (cond
      (:graph graph) (assoc (storage-graph->rich-fn storage graph) :graph-name graph-name)
      graph graph ;; base rich-fn
      :else (throw (ex-info "Unexisted graph name" {:graph-name graph-name})))))
