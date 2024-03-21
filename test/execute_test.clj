(ns execute-test
  (:require
   [cljamp.graph.execute :as sut]
   [cljamp.graph.storage :as storage]
   [clojure.test :refer [deftest is testing]]
   [common :refer [test-united-storage test-ex-info]]))

(deftest graph-name+args->execute-test
  (doall (map (fn [graph-name]
                (testing (str graph-name)
                  (doall (map (fn [{:keys [args return]}]
                                (is (= return
                                       ((sut/graph-name+args->execute test-united-storage graph-name args)))))
                              (->> graph-name
                                   (storage/name->graph test-united-storage)
                                   :tests)))))
              (storage/graph-names test-united-storage)))
  (testing "Unexisted graph name"
    (test-ex-info #(sut/graph-name+args->execute test-united-storage
                                                :unexisted-node
                                                {})
                  "Unexisted graph name"
                  {:graph-name :unexisted-node})))

#_(deftest resolve-internal-node-test
  (testing "Unexisted argument"
    (test-ex-info #(sut/resolve-internal-node test-united-storage
                                              {}
                                              {}
                                              :foo)
                  "Unexisted argument"
                  {:arg-name :foo})))

#_(deftest resolve-arg-test)

#_(deftest resolve-args-test)

#_(deftest test-test
  (let [graph-name :map]
    (doall (map (fn [{:keys [args return]}]
                  (is (= return
                         (sut/graph-name+args->execute test-united-storage graph-name args))))
                (->> graph-name
                     (storage/name->graph test-united-storage)
                     :tests)))))
