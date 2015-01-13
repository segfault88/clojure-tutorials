(ns tutorial1.producers.transit-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [tutorial1.producers.producer :as p]
            [tutorial1.producers.transit :as transit]))

(def test-file "quipper_test_file.dat")
(def quip "If at first you don't succeed... so much for skydiving.")
(def mquip (str "First the doctor told me the good news: \n"
                "I was going to have a disease named after me."))

(def t (tutorial1.producers.transit.TransitProducer. test-file))

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
  (p/add-quip t quip)
  (is (and (= (p/count-quips t) 1)
           (= (p/get-quip t) quip))))

(deftest add-2-quips
  []
  (dotimes [n 2] (p/add-quip t quip))
  (is (and (= (p/count-quips t) 2)
           (= (p/get-quip t) quip))))

(deftest add-multiline-quip
  []
  (p/add-quip t mquip)
  (is (and (= (p/count-quips t) 1)
           (= (p/get-quip t) mquip))))

(deftest add-2-multiline-quips
  []
  (dotimes [n 2] (p/add-quip t mquip))
  (is (and (= (p/count-quips t) 2)
           (= (p/get-quip t) mquip))))

(deftest get-quip
  []
  (p/add-quip t quip)
  (is (= (p/get-quip t) quip)))

(deftest get-all-quips
  []
  (dotimes [n 42] (p/add-quip t mquip))
  (is (= (p/all-quips t) (take 42 (repeat mquip)))))

(deftest drop-quips
  []
  (spit test-file "there is stuff here")
  (p/drop-quips t)
  (is (= (io/delete-file test-file :silent) :silent)))

(deftest count-quips
  []
  (dotimes [n 42] (p/add-quip t mquip))
  (is (= (p/count-quips t) 42)))
