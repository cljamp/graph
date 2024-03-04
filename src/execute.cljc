(ns execute
  (:require
   [utils :refer [->action-name]]))

(defmulti execute-action ->action-name)

#_(defn low-level-action? [multimethod action-name]
    (not= (get-method multimethod action-name)
          (get-method multimethod :default)))

#_(defmethod execute-action :default
    [get-common-action-fn action-name args]
    (let [{[next-action-name next-args] :return} (get-common-action-fn action-name)]))
