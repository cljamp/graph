(ns spec-test
  (:require
   [cljamp.graph.spec :as sut]
   [cljamp.graph.storage :as storage]
   [clojure.test :refer [deftest is testing]]
   [common :refer [test-united-storage test-ex-info]]))

(deftest graph-name->spec-test
  (doall (map (fn [graph-name]
                (testing (str graph-name)
                  (is (= (get-in (storage/name->graph test-united-storage
                                                   graph-name)
                                 [:spec
                                  :args])
                         (sut/graph-name->spec test-united-storage graph-name)))))
              (storage/graph-names test-united-storage)))
  (testing "with Unexisted graph name"
    (test-ex-info #(sut/graph-name->spec test-united-storage
                                        :unxexisted-node)
                  "Unexisted graph name"
                  {:graph-name :unxexisted-node})))

(deftest graph+default-spec+cached-spec+arg->spec-test
  (testing "with fixed arg"
    (is (= {}
           (sut/graph+default-spec+cached-spec+arg->spec test-united-storage
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:template "%s"]))))
  (testing "with named arg"
    (is (= {:foo :string}
           (sut/graph+default-spec+cached-spec+arg->spec test-united-storage
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:template :foo]))))
  (testing "with fixed arg in list"
    (is (= {}
           (sut/graph+default-spec+cached-spec+arg->spec test-united-storage
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:values ["foo"]]))))
  (testing "with two fixed args in list"
    (is (= {}
           (sut/graph+default-spec+cached-spec+arg->spec test-united-storage
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:values ["foo" "bar"]]))))
  (testing "with named arg in list"
    (is (= {:foo :any}
           (sut/graph+default-spec+cached-spec+arg->spec test-united-storage
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:values [:foo]]))))

  (testing "with two fixed args in list"
    (is (= {:foo :any
            :bar :any}
           (sut/graph+default-spec+cached-spec+arg->spec test-united-storage
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:values [:foo :bar]]))))

  (testing "with mixed args in list"
    (is (= {:bar :any}
           (sut/graph+default-spec+cached-spec+arg->spec test-united-storage
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:values ["foo" :bar]]))))
  (testing "with graph"
    (is (= {:values [:any]}
           (sut/graph+default-spec+cached-spec+arg->spec test-united-storage
                                                         {:frmt-str [:format-str {:template "%s"
                                                                                  :values :values}]}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:values [:frmt-str]]))))
  (testing "with cached"
    (is (= {:foo :foo-spec}
           (sut/graph+default-spec+cached-spec+arg->spec test-united-storage
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {:foo :foo-spec}
                                                         [:foo :bar]))))

  (testing "with unexpected arg"
    (test-ex-info #(sut/graph+default-spec+cached-spec+arg->spec test-united-storage
                                                                 {}
                                                                 {:template :string}
                                                                 {}
                                                                 [:foo :bar])
                  "Unexpected arg"
                  {:arg :foo})))

(deftest node+graph->spec-test
  (testing "Undetermined args"
    (test-ex-info #(sut/node+graph->spec test-united-storage
                                         [:format-str
                                          {:template "%s"}]
                                         {})
                  "Undetermined args"
                  {:undetermined-args #{:values}}))
  (is (= {:values [:any]}
         (sut/node+graph->spec test-united-storage
                               [:format-str
                                {:template "%s"
                                 :values :values}]
                               {}))))

(deftest graph-name->return-spec-test
  (doall (map (fn [graph-name]
                (testing (str graph-name)
                  (is (= (->> graph-name
                              (storage/name->graph test-united-storage)
                              :spec
                              :return)
                         (sut/graph-name->return-spec test-united-storage graph-name)))))
              (storage/graph-names test-united-storage)))
  (testing "with Unexisted graph name"
    (test-ex-info #(sut/graph-name->return-spec test-united-storage
                                               :unxexisted-node)
                  "Unexisted graph name"
                  {:graph-name :unxexisted-node})))
