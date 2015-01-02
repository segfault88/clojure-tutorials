(ns tutorial1.producers.file-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [tutorial1.producers.file :as file]))

(def test-file "quipper_test_file.txt")
(def quip "If at first you don't succeed... so much for skydiving.")
(def mquip (str "First the doctor told me the good news: \n"
                "I was going to have a disease named after me."))

(defn delete-test-file
  "Delete the test file silently"
  []
  io/delete-file test-file true)

(defn clean-up-fixture
  [f]
  (println "before")
  (f)
  (println "after")
  (delete-test-file))

(defn create-test-file-with
  [content]
  (spit test-file content))

(use-fixtures :each clean-up-fixture)

(deftest foo
  (is (= (file/wtf) 7)))

(run-all-tests)
