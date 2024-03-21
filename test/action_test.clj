(ns action-test
  (:require
   [cljamp.graph.action :as sut]
   [clojure.test :refer [deftest is]]))

(deftest action-class-test
  (let [test-action-name :plus
        test-action-spec {:args {:values [:number]}
                          :return :number}
        test-action-tests [{:args {:values [1 1]}
                            :return 2}]
        test-action-fn (fn [{:keys [values]}] (apply + values))
        test-action (sut/->action test-action-name
                                  test-action-spec
                                  test-action-tests
                                  test-action-fn)]
    (is (= test-action-name
           (sut/graph-name test-action)))
    (is (= test-action-name
           (:graph-name test-action)))
    (is (= test-action-spec
           (sut/spec test-action)))
    (is (= test-action-tests
           (sut/tests test-action)))
    (is (= test-action-fn
           (sut/func test-action)))))
