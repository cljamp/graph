(ns common
  (:require
   [cljamp.graph.storage :as storage]
   [clojure.test :refer [is]]))

(def test-data
  {:format-str
   {:tests [{:args {:template "%s"
                    :values ["foo"]}
             :return "foo"}]
    :spec {:args {:template :string
                  :values [:any]}
           :return :string}}

   :map
   {:tests [{:args {:func (fn [{:keys [template values]}]
                            (apply (partial format
                                            template)
                                   values))
                    :values [{:template "%s"
                              :values ["foo"]}
                             {:template "%s"
                              :values ["bar"]}]}
             :return ["foo" "bar"]}]
    :spec {:args {:func :fn
                  :values [:any]}
           :return [:any]}}

   :determine-one-arg
   {:spec {:return :string
           :args {:values [:any]}}
    :tests [{:args {:values ["foo"]}
             :return "foo"}]}

   :restrict-list
   {:spec {:return :string
           :args {:template :string
                  :str-value :any}}
    :tests [{:args {:template "%s"
                    :str-value "foo"}
             :return "foo"}]}

   :restricted-list-by-two
   {:spec {:return :string
           :args {:template :string
                  :first-str :any
                  :second-str :any}}
    :tests [{:args {:template "%s %s"
                    :first-str "foo"
                    :second-str "bar"}
             :return "foo bar"}]}

   :additional-node
   {:spec {:return :string
           :args {:template :string
                  :values [:any]}}
    :tests [{:args {:template "%s"
                    :values ["foo"]}
             :return "foo"}]}

   :nested-additional-nodes
   {:spec {:return :string
           :args {:first-name :any
                  :second-name :any}}
    :tests [{:return "Hello, dear Foo Bar!"
             :args {:first-name "Foo"
                    :second-name "Bar"}}]}

   :two-funcs-with-default-argument
   {:spec {:return :string
           :args {:str1 :any
                  :str2 :any}}
    :tests [{:args {:str1 "foo"
                    :str2 "bar"}
             :return "foo bar"}]}

   :use-external-top-level-fn
   {:spec {:return :string
           :args {:first-name :any}}
    :tests [{:args {:first-name "Foo"}
             :return "Hello, dear Foo !"}]}

   :first-class-func
   {:spec {:return [:string]
           :args {:return-values [{:fn-template :str
                                   :fn-values [:any]}]}}
    :tests [{:args {:return-values [{:fn-template "%s"
                                     :fn-values ["foo"]}
                                    {:fn-template "%s"
                                     :fn-values ["bar"]}]}
             :return ["foo" "bar"]}]}

   :first-class-func-with-one-specified-arg
   {:spec {:return [:string]
           :args {:return-values [{:fn-values [:any]}]}}
    :tests [{:args {:return-values [{:fn-values ["foo"]}
                                    {:fn-values ["bar"]}]}
             :return ["foo" "bar"]}]}

   :second-first-class-func-with-one-specified-arg
   {:spec {:return [:string]
           :args {:return-values [:any]}}
    :tests [{:args {:return-values ["foo" "bar"]}
             :return ["foo" "bar"]}]}})

(def test-graph
  {:determine-one-arg
   {:graph {:return [:format-str {:template "%s"
                                  :values :values}]}}

   :restrict-list
   {:graph {:return [:format-str {:template :template
                                  :values [:str-value]}]}}

   :restricted-list-by-two
   {:graph {:return [:format-str {:template :template
                                  :values [:first-str :second-str]}]}}

   :additional-node
   {:graph {:return [:format-str {:template "%s"
                                  :values [:formatted-string]}]
            :formatted-string [:format-str {:template :template
                                            :values :values}]}}

   :nested-additional-nodes
   {:graph {:return [:format-str {:template "Hello, %s!"
                                  :values [:dear]}]
            :dear [:format-str {:template "dear %s"
                                :values [:full-name]}]
            :full-name [:format-str {:template "%s %s"
                                     :values [:first-name :second-name]}]}}

   :two-funcs-with-default-argument
   {:graph {:return [:format-str {:template "%s %s"
                                  :values [:first-str :second-str]}]
            :first-str [:format-str {:template :template
                                     :values [:str1]}]
            :second-str [:format-str {:template :template
                                      :values [:str2]}]
            :template "%s"}}

   :use-external-top-level-fn
   {:graph {:return [:nested-additional-nodes {:first-name :first-name
                                               :second-name ""}]}}

   :first-class-func
   {:graph {:return [:map {:func :format-str
                           :values :return-values}]
            :format-str [:format-str {:template :fn-template
                                      :values :fn-values}]}}

   :first-class-func-with-one-specified-arg
   {:graph {:return [:map {:func :format-str
                           :values :return-values}]
            :format-str [:format-str {:template "%s"
                                      :values :fn-values}]}}

   :second-first-class-func-with-one-specified-arg
   {:graph {:return [:map {:func :format-str
                           :values :return-values}]
            :format-str [:format-str {:template "%s"
                                      :values :return-values}]}}})

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
    (is (= expected-msg (ex-message result)))
    (is (= expected-data (ex-data result)))))
