(ns traversal-test
  (:require
   [cljamp.graph.storage :as storage]
   [cljamp.graph.traversal :as sut]
   [clojure.test :refer [deftest is testing]]
   [common :refer [test-united-storage test-ex-info test-data]]))

(deftest storage-graph-name->action-test
    (doall (map (fn [[graph-name {{expected-args-spec :args
                                   expected-return-spec :return} :spec
                                  tests :tests}]]
                  (testing (str graph-name)
                    (let [{func :func
                           {args-spec :args
                            return-spec :return} :spec}
                          (sut/storage-graph-name->rich-fn test-united-storage graph-name)]
                      (doall (map (fn [{:keys [return args]}]
                                    (is (= return
                                           (func args))))
                                  tests))
                      (is (= expected-args-spec
                             args-spec))
                      (is (= expected-return-spec
                             return-spec)))))
                test-data))
    (testing "Unexisted name"
      (test-ex-info (sut/storage-graph-name->rich-fn test-united-storage
                                                      :unexisted-node)
                    "Unexisted name"
                    {:graph-name :unexisted-node})))

(deftest test-test
  (let [graph-name :determine-one-arg
        {{expected-args-spec :args
          expected-return-spec :return} :spec
         tests :tests} (or (->> graph-name
                                (get test-data))
                           (->> graph-name
                                (storage/name->graph test-united-storage)))
        {func :func
         {args-spec :args
          return-spec :return} :spec}
        (sut/storage-graph-name->rich-fn test-united-storage graph-name)]
    (doall (map (fn [{:keys [return args]}]
                  (is (= return
                         (func args))))
                tests))
    (is (= expected-args-spec
           args-spec))
    (is (= expected-return-spec
           return-spec))))
