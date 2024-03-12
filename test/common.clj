(ns common
  (:require
   [cljamp.graph.storage :as storage]))

(def test-graph
  {:determine-one-arg
   {:graph {:return [:format-str {:template "%s"}]}
    :spec {:return :string
           :args {:values [:any]}}}

   :restrict-list
   {:graph {:return [:format-str {:values [:str-value]}]}
    :spec {:return :string
           :args {:template :string
                  :str-value :any}}}

   :restricted-list-by-two
   {:graph {:return [:format-str {:values [:first-str :second-str]}]}
    :spec {:return :string
           :args {:template :string
                  :first-str :any
                  :second-str :any}}}

   :additional-node
   {:graph {:return [:format-str {:template "%s"
                                  :values [:formatted-string]}]
            :formatted-string :format-str}
    :spec {:return :string
           :args {:formatted-string {:template :string
                                     :values [:any]}}}}

   :nested-additional-nodes
   {:graph {:return [:format-str {:template "Hello, %s!"
                                  :values [:dear]}]
            :dear [:format-str {:template "dear %s"
                                :values [:full-name]}]
            :full-name [:format-str {:template "%s %s"
                                     :values [:first-name :second-name]}]}
    :spec {:return :string
           :args {:first-name :any
                  :second-name :any}}}})

(def test-dynamic-storage
  (storage/->map-storage test-graph))

(def test-united-storage
  (storage/->united-storage test-dynamic-storage))
