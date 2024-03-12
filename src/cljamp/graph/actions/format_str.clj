(ns cljamp.graph.actions.format-str
  (:require
   [cljamp.graph.action :refer [->action]]
   [clojure.core :refer [format]]))

(def action
  (->action :format-str
            {:args {:template :string
                    :values [:any]}
             :return :string}
            (fn [{:keys [template values]}] (apply (partial format template) values))))
