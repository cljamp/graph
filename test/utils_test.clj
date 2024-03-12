(ns utils-test
  (:require
   [cljamp.graph.utils :as sut]
   [clojure.test :refer [deftest is]]))

(deftest select-keys-exclude-test
  (is (= {:foo 1}
         (sut/select-keys-exclude {:foo 1 :bar 2} [:bar]))))
