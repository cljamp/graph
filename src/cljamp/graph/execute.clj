(ns cljamp.graph.execute
  (:require
   [cljamp.graph.storage :as storage]))

(declare graph-name+args->execute)
(declare resolve-args)

(defn resolve-internal-node
  [storage graph args-spec resolved-args arg-spec internal-name]
  (if-let [resolved-arg (get resolved-args internal-name)]
    resolved-arg
    (if-let [node (get graph
                       internal-name)]
      (if (vector? node)
        (let [execute (partial graph-name+args->execute
                               storage
                               (first node))]
          (if (= arg-spec :fn)
            (partial execute true)
            ((execute (resolve-args storage
                                    graph
                                    args-spec
                                    resolved-args
                                    (second node))))))
        node)
      (throw (ex-info "Unexisted argument"
                      {:arg-name internal-name})))))

(defn resolve-arg
  [storage graph args-spec resolved-args [arg-name arg-value]]
  (let [resolve-node (partial resolve-internal-node
                              storage
                              graph
                              args-spec
                              resolved-args
                              (get args-spec arg-name))]
    {arg-name (cond
                (keyword? arg-value) (resolve-node arg-value)
                (vector? arg-value) (mapv resolve-node arg-value)
                :else arg-value)}))

(defn resolve-args
  [storage graph args-spec resolved-args unresolved-args]
  (apply merge
         (map #(resolve-arg storage
                            graph
                            args-spec
                            resolved-args
                            %)
              unresolved-args)))

(defn graph-name+args->execute
  ([storage graph-name args] (graph-name+args->execute storage graph-name false args))
  ([storage graph-name first-class? args]
   (let [{{[next-graph-name next-args] :return
           :as graph} :graph
          {_args-spec :args} :spec
          :keys [func]} (storage/name->graph storage graph-name)]
    ;; TODO check args with args-spec
     (cond
       next-graph-name (graph-name+args->execute storage
                                                 next-graph-name
                                                 (resolve-args storage
                                                               graph
                                                               (->> next-graph-name
                                                                    (storage/name->graph storage)
                                                                    :spec
                                                                    :args)
                                                               args
                                                               next-args))
       func (let [execute #(func args)]
              (if first-class?
                (execute)
                execute))
       :else (throw (ex-info "Unexisted graph name" {:graph-name graph-name}))))))
