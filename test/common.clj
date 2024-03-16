(ns common
  (:require
   [cljamp.graph.storage :as storage]
   [clojure.test :refer [is]]))

(def test-graph
  {:determine-one-arg
   {:graph {:return [:format-str {:template "%s"
                                  :values :values}]}
    :spec {:return :string
           :args {:values [:any]}}
    :tests [{:args {:values ["foo"]}
             :return "foo"}]}

   :restrict-list
   {:graph {:return [:format-str {:template :template
                                  :values [:str-value]}]}
    :spec {:return :string
           :args {:template :string
                  :str-value :any}}
    :tests [{:args {:template "%s"
                    :str-value "foo"}
             :return "foo"}]}

   :restricted-list-by-two
   {:graph {:return [:format-str {:template :template
                                  :values [:first-str :second-str]}]}
    :spec {:return :string
           :args {:template :string
                  :first-str :any
                  :second-str :any}}}

   :additional-node
   {:graph {:return [:format-str {:template "%s"
                                  :values [:formatted-string]}]
            :formatted-string [:format-str {:template :template
                                            :values :values}]}
    :spec {:return :string
           :args {:template :string
                  :values [:any]}}}

   :nested-additional-nodes
   {:graph {:return [:format-str {:template "Hello, %s!"
                                  :values [:dear]}]
            :dear [:format-str {:template "dear %s"
                                :values [:full-name]}]
            :full-name [:format-str {:template "%s %s"
                                     :values [:first-name :second-name]}]}
    :spec {:return :string
           :args {:first-name :any
                  :second-name :any}}}

   :two-funcs-with-default-argument
   {:graph {:return [:format-str {:template "%s %s"
                                  :values [:first-str :second-str]}]
            :first-str [:format-str {:template :template
                                     :values [:str1]}]
            :second-str [:format-str {:template :template
                                      :values [:str2]}]
            :template "%s"}
    :spec {:return :string
           :args {:str1 :any
                  :str2 :any}}}})

(def test-dynamic-storage
  (storage/->map-storage test-graph))

(def test-united-storage
  (storage/->united-storage test-dynamic-storage))

(defn test-ex-info
  [f expected-msg expected-data]
  (let [result (try
                 (f)
                 (catch clojure.lang.ExceptionInfo e
                   e))]
    (is (instance? clojure.lang.ExceptionInfo result))
    (is (= expected-msg (.getMessage result)))
    (is (= expected-data (ex-data result)))))
