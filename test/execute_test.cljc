(ns execute-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [common :refer [get-test-common-node]]
   [execute :as sut]))

(deftest exectute-action-test
  (testing "with low-level action"
    (is (= "Hello, World!"
           (sut/execute-action get-test-common-node :format-str {:template "Hello, %s!"
                                                                 :values ["World"]}))))
  #_(testing "with low-level action as return"
      (is (= "Hi, World!\nBye, World!"
             (sut/execute-action get-test-common-action :hi-bye-dear-single {:somebody "World"}))))
  #_(testing "with high-level action as return"
      (is (= "Hi, World!"
             (sut/execute-action get-test-common-action :hi {:somebody "World"})))))
