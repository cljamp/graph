(ns cljamp.graph.rich-fn
  (:require
   [clojure.set :as set]))

(defrecord RichFn [graph-name spec func])

(defn ->rich-fn
  [{:keys [graph-name spec func]}]
  (->RichFn graph-name spec func))

(defmulti rich-fn-spec (fn [{:keys [graph-name]}] graph-name))

(defmethod rich-fn-spec :default [rich-fn] (:spec rich-fn))

(defn carry
  [{:keys [graph-name func]
    {args-spec :args
     return-spec :return} :spec} [arg-name arg-value]]
  (when (not (contains? args-spec arg-name))
    (throw (ex-info "Unexisted arg-name" {:arg-name arg-name})))
  (let [->r-fn (fn [[args-spec func]] (->rich-fn {:graph-name graph-name
                                           :spec {:args args-spec
                                                  :return return-spec}
                                           :func func}))]
    (->r-fn (cond

              (keyword? arg-value)
              [(set/rename-keys args-spec {arg-name arg-value})
               (fn [args]
                 (func (set/rename-keys args {arg-value arg-name})))]

              (vector? arg-value)
              (let [arg-spec (get-in args-spec [arg-name 0])]
                (reduce (fn [[args-spec func] arg-value]
                          [(cond
                             (keyword? arg-value)
                             (assoc args-spec arg-value arg-spec)

                             (instance? RichFn arg-value)
                             (->> arg-value
                                  :spec
                                  :args
                                  (merge args-spec))

                             :else args-spec)
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
                        [(dissoc args-spec arg-name) func]
                        (reverse arg-value)))

              (instance? RichFn arg-value)
              (let [{arg-spec :spec
                     arg-func :func} arg-value
                    arg-names (-> arg-spec
                                  :args
                                  keys)]
                [(merge args-spec (:args arg-spec)) ;; TODO check what happen if duplicated
                 (fn [args]
                   (func (dissoc (assoc args
                                        arg-name
                                        (if (= :fn (get args-spec arg-name))
                                          arg-func
                                          (arg-func (select-keys args arg-names))))
                                 arg-names)))])

              :else
              [(dissoc args-spec arg-name)
               (fn [args]
                 (func (assoc args arg-name arg-value)))]))))
