(ns traversal-test
  (:require
   [cljamp.graph.traversal :as sut]
   [clojure.test :refer [deftest is testing]]
   [common :refer [test-united-storage test-ex-info test-data]]))

(deftest storage-graph-name->rich-fn-test
    (doall (map (fn [[expected-graph-name {{expected-args-spec :args
                                            expected-return-spec :return} :spec
                                           tests :tests}]]
                  (testing (str expected-graph-name)
                    (let [{:keys [func graph-name]
                           {args-spec :args
                            return-spec :return} :spec}
                          (sut/storage-graph-name->rich-fn test-united-storage expected-graph-name)]
                      (doall (map (fn [{:keys [return args]}]
                                    (is (= return
                                           (func args))))
                                  tests))
                      (is (= expected-graph-name
                             graph-name))
                      (is (= expected-args-spec
                             args-spec))
                      (is (= expected-return-spec
                             return-spec)))))
                test-data))
    (testing "Unexisted name"
      (test-ex-info #(sut/storage-graph-name->rich-fn test-united-storage
                                                      :unexisted-node)
                    "Unexisted graph name"
                    {:graph-name :unexisted-node})))

(deftest single-test
  (let [expected-graph-name :first-class-func
        {{expected-args-spec :args
          expected-return-spec :return} :spec
         tests :tests} (get test-data expected-graph-name)
        {:keys [func graph-name]
         {args-spec :args
          return-spec :return} :spec}
        (sut/storage-graph-name->rich-fn test-united-storage expected-graph-name)]
    (doall (map (fn [{:keys [return args]}]
                  (is (= return
                         (func args))))
                tests))
    (is (= expected-graph-name
           graph-name))
    (is (= expected-args-spec
           args-spec))
    (is (= expected-return-spec
           return-spec))))
