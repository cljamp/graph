(ns cljamp.graph.base-rich-fns.map
  (:require
   [cljamp.graph.rich-fn :refer [->rich-fn]]))

(def rich-fn
  (->rich-fn {:graph-name :map
              :spec {:args {:func :fn
                            :values [:any]}
                     :return [:any]}
              :func (fn [{:keys [func values]}] (mapv func values))}))
