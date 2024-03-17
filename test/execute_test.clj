(ns execute-test
  (:require
   [cljamp.graph.execute :as sut]
   [clojure.test :refer [deftest is testing]]
   [cljamp.graph.storage :as storage]
   [common :refer [test-united-storage]]))

(deftest node-name+args->execute-test
  (doall (map (fn [node-name]
                (testing (str node-name)
                  (doall (map (fn [{:keys [args return]}]
                                (is (= return
                                       (sut/node-name+args->execute test-united-storage node-name args))))
                              (->> node-name
                                   (storage/get-node test-united-storage)
                                   :tests)))))
              (storage/get-node-names test-united-storage))))
