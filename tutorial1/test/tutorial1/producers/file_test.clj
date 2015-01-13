(ns tutorial1.producers.file-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [tutorial1.producers.producer :as p]
            [tutorial1.producers.file :as file]))

(def test-file "quipper_test_file.txt")
(def quip "If at first you don't succeed... so much for skydiving.")
(def mquip (str "First the doctor told me the good news: \n"
                "I was going to have a disease named after me."))
(def emquip (file/escape-newlines mquip))

(def f (tutorial1.producers.file.FileProducer. test-file))

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
  (p/add-quip f quip)
  (is (= (slurp test-file) (str/join [quip "\n"]))))

(deftest add-2-quips
  []
  (dotimes [n 2] (p/add-quip f quip))
  (is (= (slurp test-file) (str/join [quip "\n" quip "\n"]))))

(deftest add-multiline-quip
  []
  (p/add-quip f mquip)
  (is (= (slurp test-file) (str/join [emquip "\n"]))))

(deftest add-2-multiline-quips
  []
  (dotimes [n 2] (p/add-quip f mquip))
  (is (= (slurp test-file) (str/join [emquip "\n" emquip "\n"]))))

(deftest get-quip
  []
  (p/add-quip f quip)
  (is (= (p/get-quip f) quip)))

(deftest get-all-quips
  []
  (spit test-file (str/join [emquip "\n" emquip "\n"]))
  (is (= (p/all-quips f) [mquip mquip])))

(deftest get-multiline-quip
  []
  (p/add-quip f mquip)
  (is (= (p/get-quip f) mquip)))

(deftest drop-quips
  []
  (spit test-file "there is stuff here")
  (p/drop-quips f)
  (is (= (io/delete-file test-file :silent) :silent)))

(deftest count-quips
  []
  (dotimes [n 42] (p/add-quip f mquip))
  (is (= (p/count-quips f) 42)))

