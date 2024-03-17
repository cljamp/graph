(ns execute-test
  (:require
   [cljamp.graph.execute :as sut]
   [cljamp.graph.storage :as storage]
   [clojure.test :refer [deftest is testing]]
   [common :refer [test-united-storage test-ex-info]]))

(deftest node-name+args->execute-test
  (doall (map (fn [node-name]
                (testing (str node-name)
                  (doall (map (fn [{:keys [args return]}]
                                (is (= return
                                       (sut/node-name+args->execute test-united-storage node-name args))))
                              (->> node-name
                                   (storage/get-node test-united-storage)
                                   :tests)))))
              (storage/get-node-names test-united-storage)))
  (testing "Unexisted node"
    (test-ex-info #(sut/node-name+args->execute test-united-storage
                                                :unexisted-node
                                                {})
                  "Unexisted node"
                  {:node-name :unexisted-node})))

(deftest resolve-internal-node-test
  (testing "Unexisted argument"
    (test-ex-info #(sut/resolve-internal-node test-united-storage
                                              {}
                                              {}
                                              :foo)
                  "Unexisted argument"
                  {:arg-name :foo})))

#_(deftest resolve-arg-test)

#_(deftest resolve-args-test)

(deftest test-test
  (let [node-name :map]
    (doall (map (fn [{:keys [args return]}]
                  (is (= return
                         (sut/node-name+args->execute test-united-storage node-name args))))
                (->> node-name
                     (storage/get-node test-united-storage)
                     :tests)))))
