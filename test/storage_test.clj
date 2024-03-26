(ns storage-test
  (:require
   [cljamp.graph.storage :as sut]
   [clojure.test :refer [deftest is testing]]))

(deftest map-storage-test
  (let [test-key :foo
        test-value 1
        test-map-storage (sut/->map-storage {test-key test-value})]
    (testing "name->graph"
      (is (= test-value
             (sut/name->graph test-map-storage test-key))))
    (testing "get unexisted-key"
      (is (nil? (sut/name->graph test-map-storage :unexisted-key))))
    (testing "graph-names"
      (is (= #{test-key}
             (sut/graph-names test-map-storage))))))

(deftest base-rich-fns-storage-test
  (is (= {:graph-name :format-str
          :spec {:args {:template :string, :values [:any]}
                 :return :string}}
         (select-keys (sut/name->graph sut/base-rich-fns-storage :format-str)
                      [:graph-name :spec]))))

(deftest ->united-storage-test
  (let [test-united-storage (sut/->united-storage (sut/->map-storage {}))]
    (is (= {:graph-name :format-str
            :spec {:args {:template :string, :values [:any]}
                   :return :string}}
           (select-keys (sut/name->graph test-united-storage :format-str)
                        [:graph-name :spec])))
    (is (= #{:format-str
             :map}
           (sut/graph-names test-united-storage)))))
