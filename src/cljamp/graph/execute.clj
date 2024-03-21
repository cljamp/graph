(ns cljamp.graph.execute
  (:require
   [cljamp.graph.storage :as storage]))

(declare graph-name+args->execute)
(declare resolve-args)

(defn resolve-internal-node
  [storage graph resolved-args internal-name]
  (if-let [resolved-arg (get resolved-args internal-name)]
    resolved-arg
    (if-let [node (get graph
                       internal-name)]
      (cond (vector? node)
            ((graph-name+args->execute storage
                                        (first node) (resolve-args storage
                                                                   graph
                                                                   resolved-args
                                                                   (second node))))

            :else node)
      (throw (ex-info "Unexisted argument"
                      {:arg-name internal-name})))))

(defn resolve-arg
  [storage graph resolved-args [arg-name arg-value]]
  {arg-name (cond
              (keyword? arg-value) (resolve-internal-node storage
                                                          graph
                                                          resolved-args
                                                          arg-value)
              (vector? arg-value) (mapv (partial resolve-internal-node
                                                 storage
                                                 graph
                                                 resolved-args)
                                        arg-value)
              :else arg-value)})

(defn resolve-args
  [storage graph resolved-args unresolved-args]
  (apply merge
         (map #(resolve-arg storage
                            graph
                            resolved-args
                            %)
              unresolved-args)))

(defn graph-name+args->execute
  [storage graph-name args]
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
                                                              args
                                                              next-args))
      func #(func args)
      :else (throw (ex-info "Unexisted graph name" {:graph-name graph-name})))))
