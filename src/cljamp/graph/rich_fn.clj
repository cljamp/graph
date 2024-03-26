(ns cljamp.graph.rich-fn
  (:require
   [clojure.set :as set]))

(defrecord RichFn [graph-name spec func])

(defn ->rich-fn
  [{:keys [graph-name spec func]}]
  (->RichFn graph-name spec func))

(defn carry
  [{:keys [graph-name spec func]} [arg-name arg-value]]
  (let [->rich-fn #(->rich-fn {:graph-name graph-name
                               :spec %1
                               :func %2})]
    (cond
      (not (contains? (:args spec) arg-name))
      (throw (ex-info "Unexisted arg-name" {:arg-name arg-name}))

      (keyword? arg-value)
      (->rich-fn (update spec :args set/rename-keys {arg-name arg-value})
                 (fn [args]
                   (func (set/rename-keys args {arg-value arg-name}))))

      (vector? arg-value)
      (let [arg-spec (get-in spec [:args arg-name 0])]
        (apply ->rich-fn
               (reduce (fn [[spec func] arg-value]
                         [(if (keyword? arg-value)
                            (assoc-in spec
                                      [:args arg-value]
                                      arg-spec)
                            spec)
                          (fn [args]
                            (func (cond-> args
                                    (keyword? arg-value) (dissoc arg-value)
                                    (not (vector? (get args arg-name))) (assoc arg-name [])
                                    :always (update arg-name conj (if (keyword? arg-value)
                                                                    (get args arg-value)
                                                                    arg-value)))))])
                       [(update spec
                                :args
                                dissoc
                                arg-name)
                        func]
                       arg-value)))

      :else
      (->rich-fn (update spec :args dissoc arg-name)
                 (fn [args]
                   (func (merge args {arg-name arg-value})))))))
