(ns logic
  (:require
   [clojure.core :refer [format]]
   [clojure.set :as clojure-set]
   [malli.util :as mu]))

(defn ->action-name
  [_get-common-action-fn action-name & _args]
  action-name)

#_(defmulti get-args-spec ->action-name)
#_(defmulti get-return-spec ->action-name)
(defmulti execute-action ->action-name)

#_(defn low-level-action? [multimethod action-name]
    (not= (get-method multimethod action-name)
          (get-method multimethod :default)))

(defn select-keys-exclude
  [m exclude-keys]
  (->> m
       keys
       (filter #(not-any? #{%} exclude-keys))
       (select-keys m)))

(declare action-name+args->args-spec)

(defn arg->arg-spec
  [get-common-action-fn args-spec arg arg-value]
  (cond
    (keyword? arg-value) {arg (action-name+args->args-spec get-common-action-fn arg-value)}
    (vector? arg-value) (let [arg-spec (get args-spec arg)]
                          (when (< 1 (count arg-spec))
                            (throw (ex-info "Too long vector spec" {:arg arg
                                                                    :spec arg-spec})))
                          (if (vector? arg-spec)
                            (let [args (->> arg-value
                                            (map #(arg->arg-spec get-common-action-fn
                                                                 {:temp-vec-arg (first arg-spec)}
                                                                 :temp-vec-arg %))
                                            (map :temp-vec-arg)
                                            (filter some?)
                                            (into []))]
                              (if (empty? args) {} {arg args}))
                            (throw (ex-info "Unexpected vector arg-value"
                                            {:arg arg
                                             :value arg-value
                                             :spec arg-spec}))))
    :else {}))

(defn external-args+internal-args-spec->external-args-spec
  [get-common-action-fn external-args args-spec]
  (let [undetermined-by-external-args (select-keys-exclude args-spec (keys external-args))]
    (->> external-args
         (map #(arg->arg-spec get-common-action-fn
                              args-spec
                              (first %)
                              (second %)))
         (apply merge)
         (merge undetermined-by-external-args))))

(defn rename-spec
  [base-spec renaming]
  (cond
    (map? base-spec) (->> base-spec
                          (map #(rename-spec (second %) (get renaming (first %))))
                          (apply merge))
    (vector? base-spec) (->> renaming
                             (map vector base-spec)
                             (map #(rename-spec (first %) (second %)))
                             (apply merge))
    (keyword? base-spec) {renaming base-spec}
    :else (throw (ex-info "Unexpected renaming-spec type" {:base-spec base-spec
                                                           :renaming renaming}))))

(defn action-name+args->args-spec
  ([get-common-action-fn action-name]
   (action-name+args->args-spec get-common-action-fn action-name {}))
  ([get-common-action-fn action-name external-args]
   (let [{[internal-action-name intermediate-args] :return
          {:keys [renaming]} :args
          {args-spec :args} :spec} (get-common-action-fn action-name)

         base-spec
         (cond
           args-spec
           (external-args+internal-args-spec->external-args-spec get-common-action-fn external-args args-spec)

           internal-action-name
           (action-name+args->args-spec get-common-action-fn
                                        internal-action-name
                                        (merge intermediate-args
                                               external-args))

           :else nil)]
     (if renaming
       (rename-spec base-spec renaming)
       base-spec))))

(defn action-name->return-spec
  [get-common-action-fn action-name]
  (let [{[next-action-name _] :return
         {return :return} :spec} (get-common-action-fn action-name)]
    (if next-action-name
      (action-name->return-spec get-common-action-fn next-action-name)
      return)))

#_(defmethod execute-action :default
    [get-common-action-fn action-name args]
    (let [{[next-action-name next-args] :return} (get-common-action-fn action-name)]))

(defmethod execute-action :format-str [_get-common-action-fn _action-name {:keys [template values]}]
  (apply (partial format template) values))

(defmethod execute-action :arg [_get-common-action-fn _action-name {:keys [value]}]
  value)
