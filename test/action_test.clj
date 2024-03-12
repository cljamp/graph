(ns action-test
  (:require
   [cljamp.graph.action :as sut]
   [clojure.test :refer [deftest is]]))

(deftest action-class-test
  (let [test-action-name :plus
        test-action-spec {:args {:values [:number]}
                          :return :number}
        test-action-fn (fn [{:keys [values]}] (apply + values))
        test-action (sut/->action test-action-name
                                  test-action-spec
                                  test-action-fn)]
    (is (= test-action-name
           (sut/node-name test-action)))
    (is (= test-action-name
           (:node-name test-action)))
    (is (= test-action-spec
           (sut/spec test-action)))
    (is (= test-action-fn
           (sut/func test-action)))))
