(ns cljamp.graph.actions.map
  (:require
   [cljamp.graph.action :refer [->action]]))

(def action
  (->action :map
            {:args {:func :any ; TODO
                    :values [:any]}
             :return [:any]}
            [{:args {:func :format-str
                     :values [{:template "%s"
                               :values ["foo"]}
                              {:template "%s"
                               :values ["bar"]}]}
              :return ["foo" "bar"]}]
            (fn [{:keys [func values]}] (mapv func values))))
