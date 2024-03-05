(ns spec-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [common :refer [get-test-common-node test-graph]]
   [spec :as sut]))

(deftest node-name->spec-test
  (doall (map (fn [[node-name node]]
                (testing (str node-name)
                  (is (= (get-in node [:spec :args])
                         (sut/node-name->spec get-test-common-node node-name)))))
              test-graph)))

(deftest node-name->return-spec-test
  (doall (map (fn [[node-name node]]
                (testing (str node-name)
                  (is (= (get-in node [:spec :return])
                         (sut/node-name->return-spec get-test-common-node node-name)))))
              test-graph)))
