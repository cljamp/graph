(ns actions.format-str
  (:require
   [clojure.core :refer [format]]
   [execute :refer [execute-action]]))

(defmethod execute-action :format-str [_get-common-action-fn _action-name {:keys [template values]}]
  (apply (partial format template) values))
