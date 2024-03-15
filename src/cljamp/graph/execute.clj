(ns cljamp.graph.execute
  (:require
   [cljamp.graph.storage :as storage]))

(defn resolve-arg
  [graph-storage args graph resolved-args [arg-name _arg-spec]]
  (assoc resolved-args
         arg-name
         (if-let [arg-value (get args arg-name)]
           arg-value)))

(defn node-name+args->execute!
  ;; Аргументы - отдельный параметр или уже добавлены в граф как ноды с фиксированным значением? 
  ;; А другие фиксированные значения тоже должты быть отдельными нодами?
  ;; Ноды с фиксированными значениями - это по сути ноды без потомков, в отличии от функций с аргументами
  ;; Поэтому, нормально определять их отделльным образом, а не так же, как ноды-фугкции через псевдонимы и глобальные ноды
  ;; Заранее определённые - как значения аргументов, переданные при запуске - как отдельный аргумент
  ;; С другой стороны, для нод-системных-костант, тогда, придётся заводить новй тип нод
  [graph-storage node-name args]
  (let [{{[next-node-name next-args] :return
          :as graph} :graph
         {args-spec :args} :spec
         :keys [func]} (storage/get-node graph-storage node-name)]
    (cond
      next-node-name (node-name+args->execute! graph-storage next-node-name (merge args next-args))
      func (func (reduce #(resolve-arg graph-storage args graph %1 %2) {} args-spec))
      :else (throw (ex-info "Unexisted node" {:node-name node-name})))))