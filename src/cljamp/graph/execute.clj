(ns cljamp.graph.execute
  (:require
   [cljamp.graph.storage :as storage]))

(declare node-name+args->execute)
(declare resolve-args)

(defn resolve-internal-node
  [graph-storage graph resolved-args internal-name]
  (if-let [resolved-arg (get resolved-args internal-name)]
    resolved-arg
    (if-let [node (get graph
                       internal-name)]
      (cond (vector? node)
            (node-name+args->execute graph-storage
                                     (first node) (resolve-args graph-storage
                                                                graph
                                                                resolved-args
                                                                (second node)))

            :else node)
      (throw (ex-info "Unexisted argument"
                      {:arg-name internal-name})))))

(defn resolve-arg
  [graph-storage graph resolved-args [arg-name arg-value]]
  {arg-name (cond
              (keyword? arg-value) (resolve-internal-node graph-storage
                                                          graph
                                                          resolved-args
                                                          arg-value)
              (vector? arg-value) (mapv (partial resolve-internal-node
                                                 graph-storage
                                                 graph
                                                 resolved-args)
                                        arg-value)
              :else arg-value)})

(defn resolve-args
  [graph-storage graph resolved-args unresolved-args]
  (apply merge
         (map #(resolve-arg graph-storage
                            graph
                            resolved-args
                            %)
              unresolved-args)))

(defn node-name+args->execute
  [graph-storage node-name args]
  (let [{{[next-node-name next-args] :return
          :as graph} :graph
         {_args-spec :args} :spec
         :keys [func]} (storage/get-node graph-storage node-name)]
    ;; TODO check args with args-spec
    (cond
      next-node-name (node-name+args->execute graph-storage
                                              next-node-name
                                              (resolve-args graph-storage
                                                            graph
                                                            args
                                                            next-args))
      func (func args)
      :else (throw (ex-info "Unexisted node" {:node-name node-name})))))