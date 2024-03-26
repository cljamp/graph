(ns cljamp.graph.base-rich-fns.format-str
  (:require
   [cljamp.graph.rich-fn :refer [->rich-fn]]
   [clojure.core :refer [format]]))

(def rich-fn
  (->rich-fn {:graph-name :format-str
              :spec {:args {:template :string
                            :values [:any]}
                     :return :string}
              :func (fn [{:keys [template values]}]
                      (apply (partial format
                                      template)
                             values))}))
