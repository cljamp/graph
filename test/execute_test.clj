(ns execute-test
  (:require
   [cljamp.graph.execute :as sut]
   [clojure.test :refer [deftest is testing]]
   [common :refer [test-united-storage]]))

(deftest node-name+args->execute!
  (is (= "foo"
         (sut/node-name+args->execute test-united-storage
                                       :format-str
                                       {:template "%s"
                                        :values ["foo"]})))
  (is (= "foo"
         (sut/node-name+args->execute test-united-storage
                                       :determine-one-arg
                                       {:values ["foo"]}))))
