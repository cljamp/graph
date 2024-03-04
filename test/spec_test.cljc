(ns spec-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [common :refer [get-test-common-action]]
   [spec :as sut]))

(deftest action-name+args->args-spec-test
  (testing "with low-level action, "
    (testing "without args"
      (is (= {:template :string
              :values [:any]}
             (sut/action-name+args->args-spec get-test-common-action :format-str))))
    (testing "with fixed arg"
      (is (= {}
             (sut/action-name+args->args-spec get-test-common-action
                                              :format-str
                                              {:template "%s"
                                               :values [{:value "Hi!"}]}))))
    (testing "with different args in list"
      (is (= {:values [{:value :any}
                       {:template :string
                        :values [:any]}]}
             (sut/action-name+args->args-spec get-test-common-action
                                              :format-str
                                              {:template "%s"
                                               :values [{:value "Hi!"} :arg :format-str]}))))
    (testing "with external restricted-list-one-args"
      (is (= {:template :string
              :values [{:value :any}]}
             (sut/action-name+args->args-spec get-test-common-action
                                              :format-str
                                              {:values [:arg]}))))
    (testing "with external restricted-list-two-args"
      (is (= {:template :string
              :values [{:value :any}
                       {:value :any}]}
             (sut/action-name+args->args-spec get-test-common-action
                                              :format-str
                                              {:values [:arg :arg]}))))
    (testing "with action restricted-list-fn-one-args"
      (is (= {:template :string
              :values [{:template :string
                        :values [:any]}]}
             (sut/action-name+args->args-spec get-test-common-action
                                              :format-str
                                              {:values [:format-str]}))))
    (testing "with action restricted-list-fn-two-args"
      (is (= {:template :string
              :values [{:template :string
                        :values [:any]}
                       {:template :string
                        :values [:any]}]}
             (sut/action-name+args->args-spec get-test-common-action
                                              :format-str
                                              {:values [:format-str :format-str]})))))
  (testing "with high-level actions"
    (testing "with low-level action as return"
      (testing "without args"
        (is (= {:values [{:value :any}]}
               (sut/action-name+args->args-spec get-test-common-action
                                                :dear))))
      (testing "with fixed arg"
        (is (= {}
               (sut/action-name+args->args-spec get-test-common-action
                                                :dear
                                                {:values [{:value "Hi"}]})))))
    (testing "with high-level action as return"
      (is (= {:values [{:values [{:value :any}]}]}
             (sut/action-name+args->args-spec get-test-common-action
                                              :hi-dear)))
      (is (= {:values
              [{:values [{:values [{:value :any}]}]}
               {:values [{:values [{:value :any}]}]}]}
             (sut/action-name+args->args-spec get-test-common-action
                                              :hi-bye-dear-different))))
    (testing "with binded args"
      (is (= {:someone :any}
             (sut/action-name+args->args-spec get-test-common-action
                                              :hi-dear-dear-bye-dear-single)))))
  #_(testing "with first-class action"
      (testing "with low-level function"
        (testing "without args"
          (is (= {}
                 (sut/action-name+args->args-spec get-test-common-action
                                                  :map))))
        (testing "with args"
          (is (= {}
                 (sut/action-name+args->args-spec get-test-common-action
                                                  :map
                                                  {:func :arg})))))))

(deftest action-name->return-spec-test
  (testing "with low-level action"
    (is (= :str
           (sut/action-name->return-spec get-test-common-action :format-str))))
  (testing "with high-level action"
    (is (= :str
           (sut/action-name->return-spec get-test-common-action :hi-dear-dear-bye-dear-single)))
    (is (= :str
           (sut/action-name->return-spec get-test-common-action :bye-dear)))))
