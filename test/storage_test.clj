(ns storage-test
  (:require
   [cljamp.graph.storage :as sut]
   [clojure.test :refer [deftest is testing]]))

(deftest map-storage-test
  (let [test-key :foo
        test-value 1
        test-map-storage (sut/->map-storage {test-key test-value})]
    (testing "get-node"
      (is (= test-value
             (sut/get-node test-map-storage test-key))))
    (testing "get unexisted-key"
      (is (nil? (sut/get-node test-map-storage :unexisted-key))))
    (testing "get-node-names"
      (is (= #{test-key}
             (sut/get-node-names test-map-storage))))))

(deftest low-level-actions-storage-test
  (is (= {:node-name :format-str
          :spec {:args {:template :string, :values [:any]}
                 :return :string}}
         (select-keys (sut/get-node sut/low-level-actions-storage :format-str)
                      [:node-name :spec]))))

(deftest ->united-storage-test
  (let [test-united-storage (sut/->united-storage (sut/->map-storage {}))]
    (is (= {:node-name :format-str
            :spec {:args {:template :string, :values [:any]}
                   :return :string}}
           (select-keys (sut/get-node test-united-storage :format-str)
                        [:node-name :spec])))
    (is (= #{:format-str}
           (sut/get-node-names test-united-storage)))))
