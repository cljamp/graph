(ns cljamp.graph.actions.map
  (:require
   [cljamp.graph.action :refer [->action]]))

(def action
  (->action :map
            {:args {:func :fn
                    :values [:any]}
             :return [:any]}
            [{:args {:func (fn [{:keys [template values]}]
                             (apply (partial format
                                             template)
                                    values))
                     :values [{:template "%s"
                               :values ["foo"]}
                              {:template "%s"
                               :values ["bar"]}]}
              :return ["foo" "bar"]}]
            (fn [{:keys [func values]}] (mapv func values))))
