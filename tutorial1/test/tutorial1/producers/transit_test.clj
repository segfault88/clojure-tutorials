(ns tutorial1.producers.transit-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [tutorial1.producers.transit :as t]))

(def test-file "quipper_test_file.dat")
(def quip "If at first you don't succeed... so much for skydiving.")
(def mquip (str "First the doctor told me the good news: \n"
                "I was going to have a disease named after me."))

(defn delete-test-file
  "Delete the test file silently"
  []
  (io/delete-file test-file true))

(defn clean-up-fixture
  "Wrap a fixture around the test file that ensures that the test file is cleaned up"
  [f]
  (delete-test-file)
  (f)
  (delete-test-file))

(use-fixtures :each clean-up-fixture)

(deftest add-quip
  []
  (t/add-quip test-file quip)
  (is (and (= (t/count-quips test-file) 1)
           (= (t/get-quip test-file) quip))))

(deftest add-2-quips
  []
  (dotimes [n 2] (t/add-quip test-file quip))
  (is (and (= (t/count-quips test-file) 2)
           (= (t/get-quip test-file) quip))))

(deftest add-multiline-quip
  []
  (t/add-quip test-file mquip)
  (is (and (= (t/count-quips test-file) 1)
           (= (t/get-quip test-file) mquip))))

(deftest add-2-multiline-quips
  []
  (dotimes [n 2] (t/add-quip test-file mquip))
  (is (and (= (t/count-quips test-file) 2)
           (= (t/get-quip test-file) mquip))))

(deftest get-quip
  []
  (t/add-quip test-file quip)
  (is (= (t/get-quip test-file) quip)))

(deftest get-all-quips
  []
  (dotimes [n 42] (t/add-quip test-file mquip))
  (is (= (t/all-quips test-file) (take 42 (repeat mquip)))))

(deftest drop-quips
  []
  (spit test-file "there is stuff here")
  (t/drop-quips test-file)
  (is (= (io/delete-file test-file :silent) :silent)))

(deftest count-quips
  []
  (dotimes [n 42] (t/add-quip test-file mquip))
  (is (= (t/count-quips test-file) 42)))
