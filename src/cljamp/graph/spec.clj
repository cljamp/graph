(ns cljamp.graph.spec
  (:require
   [cljamp.graph.storage :as storage]
   [cljamp.graph.utils :refer [select-keys-exclude]]))

(declare node+graph->spec)

(defn node-name->spec
  [graph-storage node-name]
  (let [{{:keys [return] :as graph} :graph
         {args-spec :args} :spec} (storage/get-node graph-storage node-name)]
    (cond
      graph (node+graph->spec graph-storage
                              return
                              graph)
      args-spec args-spec
      :else (throw (ex-info "Unexisted node" {:node-name node-name})))))

(defn graph+default-spec+cached-spec+arg->spec
  [graph-storage graph default-spec cached-spec [arg-name arg]]
  (if (some? (get cached-spec arg-name))
    cached-spec
    (let [default-spec (get default-spec arg-name)]
      (when (nil? default-spec) (throw (ex-info "Unexpected arg" {:arg arg-name})))
      (cond
        (keyword? arg)
        (let [node (get graph arg)]
          (cond
            (vector? node) (merge cached-spec
                                  (node+graph->spec graph-storage node graph))
            (nil? node) (assoc cached-spec arg default-spec)
            #_(keyword? node) ;; TODO for first-class functions?
            :else cached-spec))

        (vector? arg)
        (reduce #(graph+default-spec+cached-spec+arg->spec graph-storage
                                                           graph
                                                           {:temp-arg (first default-spec)}
                                                           %1
                                                           [:temp-arg %2])
                cached-spec
                arg)

        :else cached-spec))))

(defn node+graph->spec
  [graph-storage [node-name args] graph]
  (let [node-spec (node-name->spec graph-storage node-name)
        undetermined-args (-> node-spec
                              (select-keys-exclude (keys args))
                              keys
                              set)]
    (if (empty? undetermined-args) 
      (reduce (partial graph+default-spec+cached-spec+arg->spec
                       graph-storage
                       graph
                       node-spec)
              {}
              args)
      (throw (ex-info "Undetermined args" {:undetermined-args undetermined-args})))))

(defn node-name->return-spec
  [graph-storage node-name]
  (let [{{[next-node-name _] :return} :graph
         {return :return} :spec} (storage/get-node graph-storage node-name)]
    (cond
      next-node-name (node-name->return-spec graph-storage next-node-name)
      return return
      :else (throw (ex-info "Unexisted node" {:node-name node-name})))))
