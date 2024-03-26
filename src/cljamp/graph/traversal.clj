(ns cljamp.graph.traversal
  (:require
   [cljamp.graph.storage :as storage]
   [cljamp.graph.rich-fn :as rich-fn]))

(declare storage-graph-name->rich-fn)

(defn extend-arg
  [storage graph [arg-name arg-value]]
   [arg-name
    (if (vector? arg-value)
      (mapv (partial extend-arg storage graph) arg-value)
      (if-let [node (get graph arg-value)]
        (if (vector? node)
          (storage-graph-name->rich-fn storage arg-value)
          arg-value)
        arg-value))])

(defn storage-graph->rich-fn
  [storage {{[template-graph-name template-args] :return
             :as graph} :graph
            {args-spec :args} :spec}]
  (reduce rich-fn/carry
          (storage-graph-name->rich-fn storage
                                      template-graph-name)
          (map (partial extend-arg
                        storage graph)
               template-args)))

(defn storage-graph-name->rich-fn
  [storage graph-name]
  (let [graph (storage/name->graph storage graph-name)]
    (cond
      (:graph graph) (assoc (storage-graph->rich-fn storage graph) :graph-name graph-name)
      graph graph ;; base rich-fn
      :else (throw (ex-info "Unexisted graph name" {:graph-name graph-name})))))
