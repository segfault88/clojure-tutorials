(ns tutorial1.producers.file-test
  (:require [clojure.test :refer :all]
            [tutorial1.producers.file :as file]))

(def test-file "quipper_test_file.txt")

(deftest foo
  (is (= (file/wtf) 7)))

(run-all-tests)
