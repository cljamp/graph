(ns spec
  (:require
   [utils :refer [select-keys-exclude]]))

(declare node-name->spec)
(declare node+graph->spec)
(declare node-name->return-spec)

(defn graph+default-spec+cached-spec+arg->spec
  [get-common-node-fn graph default-spec cached-spec [arg-name arg]]
  (if (some? (get cached-spec arg-name))
    cached-spec
    (let [default-spec (get default-spec arg-name)]
      (cond
        (keyword? arg)
        (let [node (get graph arg)]
          (cond
            (keyword? node) (assoc cached-spec
                                   arg
                                   (node-name->spec get-common-node-fn node))
            (vector? node) (merge cached-spec
                                  (node+graph->spec get-common-node-fn node graph))
            (nil? node) (assoc cached-spec arg default-spec)
            :else (throw (ex-info "Unexpected node type" {:node node}))))

        (vector? arg)
        (reduce #(graph+default-spec+cached-spec+arg->spec
                  get-common-node-fn
                  graph
                  {:temp-arg (first default-spec)}
                  %1
                  [:temp-arg %2])
                cached-spec
                arg)

        :else cached-spec))))

(defn node+graph->spec
  [get-common-node-fn [node-name args] graph]
  (let [node-spec (node-name->spec get-common-node-fn node-name)
        undetermined-args (select-keys-exclude node-spec (keys args))]
    (merge undetermined-args
           (reduce (partial graph+default-spec+cached-spec+arg->spec
                            get-common-node-fn
                            graph
                            node-spec)
                   {}
                   args))))

(defn node-name->spec
  [get-common-node-fn node-name]
  (let [{{:keys [return] :as graph} :graph
         {args-spec :args} :spec} (get-common-node-fn node-name)]
    (cond
      graph (node+graph->spec get-common-node-fn
                              return
                              graph)

      args-spec args-spec
      :else nil)))

(defn node-name->return-spec
  [get-common-node-fn node-name]
  (let [{{[next-node-name _] :return} :graph
         {return :return} :spec} (get-common-node-fn node-name)]
    (if next-node-name
      (node-name->return-spec get-common-node-fn next-node-name)
      return)))
