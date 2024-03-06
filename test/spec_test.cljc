(ns spec-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [common :refer [get-test-common-node test-graph]]
   [spec :as sut])
  (:import
   (clojure.lang
    ExceptionInfo)))

(deftest node-name->spec-test
  (doall (map (fn [[node-name node]]
                (testing (str node-name)
                  (is (= (get-in node [:spec :args])
                         (sut/node-name->spec get-test-common-node node-name)))))
              test-graph))
  (testing "with unexisted node"
    (try (sut/node-name->spec get-test-common-node
                              :unxexisted-node)
         (catch ExceptionInfo e
           (is (= {:node-name :unxexisted-node}
                  (ex-data e)))
           (is (= "Unexisted node"
                  (ex-message e)))))))

(deftest graph+default-spec+cached-spec+arg->spec-test
  (testing "with fixed arg"
    (is (= {}
           (sut/graph+default-spec+cached-spec+arg->spec get-test-common-node
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:template "%s"]))))
  (testing "with named arg"
    (is (= {:foo :string}
           (sut/graph+default-spec+cached-spec+arg->spec get-test-common-node
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:template :foo]))))
  (testing "with fixed arg in list"
    (is (= {}
           (sut/graph+default-spec+cached-spec+arg->spec get-test-common-node
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:values ["foo"]]))))
  (testing "with two fixed args in list"
    (is (= {}
           (sut/graph+default-spec+cached-spec+arg->spec get-test-common-node
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:values ["foo" "bar"]]))))
  (testing "with named arg in list"
    (is (= {:foo :any}
           (sut/graph+default-spec+cached-spec+arg->spec get-test-common-node
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:values [:foo]]))))

  (testing "with two fixed args in list"
    (is (= {:foo :any
            :bar :any}
           (sut/graph+default-spec+cached-spec+arg->spec get-test-common-node
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:values [:foo :bar]]))))

  (testing "with mixed args in list"
    (is (= {:bar :any}
           (sut/graph+default-spec+cached-spec+arg->spec get-test-common-node
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:values ["foo" :bar]]))))
  (testing "with graph"
    (is (= {:frmt-str {:template :string, :values [:any]}}
           (sut/graph+default-spec+cached-spec+arg->spec get-test-common-node
                                                         {:frmt-str :format-str}
                                                         {:template :string, :values [:any]}
                                                         {}
                                                         [:values [:frmt-str]]))))
  (testing "with cached"
    (is (= {:foo :foo-spec}
           (sut/graph+default-spec+cached-spec+arg->spec get-test-common-node
                                                         {}
                                                         {:template :string, :values [:any]}
                                                         {:foo :foo-spec}
                                                         [:foo :bar]))))

  (testing "with incorrect node type"
    (try (sut/graph+default-spec+cached-spec+arg->spec get-test-common-node
                                                       {:bar {}}
                                                       {:template :string, :values [:any]}
                                                       {}
                                                       [:foo :bar])
         (catch ExceptionInfo e
           (is (= {:node {}}
                  (ex-data e)))
           (is (= "Unexpected node type"
                  (ex-message e)))))))

(deftest node+graph->spec-test
  (is (= {:values [:any]}
         (sut/node+graph->spec get-test-common-node
                               [:format-str
                                {:template "%s"}]
                               {}))))

(deftest node-name->return-spec-test
  (doall (map (fn [[node-name node]]
                (testing (str node-name)
                  (is (= (get-in node [:spec :return])
                         (sut/node-name->return-spec get-test-common-node node-name)))))
              test-graph))
  (testing "with unexisted node"
    (try (sut/node-name->return-spec get-test-common-node
                                     :unxexisted-node)
         (catch ExceptionInfo e
           (is (= {:node-name :unxexisted-node}
                  (ex-data e)))
           (is (= "Unexisted node"
                  (ex-message e)))))))
