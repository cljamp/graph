(ns rich-fn-test
  (:require
   [cljamp.graph.rich-fn :as sut]
   [clojure.test :refer [deftest testing is]]))

(def test-fn-name :plus)
(def test-fn-spec {:args {:values [:number]}
                       :return :number})
(def test-fn-fn (fn [{:keys [values]}] (apply + values)))
(def test-fn (sut/->rich-fn {:graph-name test-fn-name
                             :spec test-fn-spec
                             :func test-fn-fn}))

(deftest carry-test
  (testing "carry"
    (testing "rename arg"
      (let [carried-fn (sut/carry test-fn [:values :new-values])]
        (is (= {:new-values [:number]}
               (get-in carried-fn [:spec :args])))
        (is (= 4
               ((:func carried-fn) {:new-values [2 2]})))))
    (testing "specify full list"
      (let [carried-fn (sut/carry test-fn [:values [2 2]])]
        (is (= {}
               (get-in carried-fn [:spec :args])))
        (is (= 4
               ((:func carried-fn) {})))))
    (testing "specify list partly"
      (let [carried-fn (sut/carry test-fn [:values [2 :second]])]
        (is (= {:second :number}
               (get-in carried-fn [:spec :args])))
        (is (= 4
               ((:func carried-fn) {:second 2})))))))
