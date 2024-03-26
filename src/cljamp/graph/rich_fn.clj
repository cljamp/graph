(ns cljamp.graph.rich-fn
  (:require
   [clojure.set :as set]))

(defrecord RichFn [graph-name spec func])

(defn ->rich-fn
  [{:keys [graph-name spec func]}]
  (->RichFn graph-name spec func))

(defn carry
  [{:keys [graph-name spec func]} [arg-name arg-value]]
  (let [->r-fn #(->rich-fn {:graph-name graph-name
                            :spec %1
                            :func %2})]
    (cond
      (not (contains? (:args spec) arg-name))
      (throw (ex-info "Unexisted arg-name" {:arg-name arg-name}))

      (keyword? arg-value)
      (->r-fn (update spec :args set/rename-keys {arg-name arg-value})
              (fn [args]
                (func (set/rename-keys args {arg-value arg-name}))))

      (vector? arg-value)
      (let [arg-spec (get-in spec [:args arg-name 0])]
        (apply ->r-fn
               (reduce (fn [[spec func] arg-value]
                         [(cond
                            (keyword? arg-value)
                            (assoc-in spec
                                      [:args arg-value]
                                      arg-spec)
                            
                            (instance? RichFn arg-value)
                            (-> arg-value
                                :spec
                                (update :args merge (get-in arg-value [:spec :args])))

                            :else spec)
                          (fn [args]
                            (func (cond-> args
                                    (keyword? arg-value) (dissoc arg-value)
                                    (not (vector? (get args arg-name))) (assoc arg-name [])
                                    :always (update arg-name conj (cond
                                                                    (keyword? arg-value)
                                                                    (get args arg-value)

                                                                    (instance? RichFn arg-value)
                                                                    ((:func arg-value) (select-keys args (keys (get-in arg-value [:spec :args]))))

                                                                    :else arg-value))
                                    (instance? RichFn arg-value) (dissoc (keys (get-in arg-value [:spec :args]))))))])
                       [(update spec
                                :args
                                dissoc
                                arg-name)
                        func]
                       (reverse arg-value))))
      
      (instance? RichFn arg-value) (let [{arg-spec :spec
                                          arg-func :func} arg-value
                                         arg-names (-> arg-spec
                                                      :args
                                                      keys)]
                                     (->r-fn (update spec :args merge (:args arg-spec)) ;; TODO check what happen if duplicated
                                           (fn [args]
                                             (func (dissoc (assoc args
                                                                  arg-name
                                                                  (arg-func (select-keys args arg-names)))
                                                           arg-names)))))

      :else
      (->r-fn (update spec :args dissoc arg-name)
              (fn [args]
                (func (assoc args arg-name arg-value)))))))
