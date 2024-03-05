(ns common)

(def test-graph
  {:format-str {:spec {:args {:template :string
                              :values [:any]}
                       :return :string}}
   :map {:spec {:args {:func :any
                       :values [:any]}
                :return [:any]}}
   ;; reduce
   ;; filter
   
   :determine-one-arg
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
                                :values [:fullname]}]
            :fullname [:format-str {:template "%s %s"
                                    :values [:first-name :second-name]}]}
    :spec {:return :string
           :args {:first-name :any
                  :second-name :any}}}})

(defn get-test-common-node
  [node-name]
  (get test-graph node-name))
