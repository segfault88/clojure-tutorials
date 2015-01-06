(ns tutorial1.producers.file-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [tutorial1.producers.file :as file]))

(def test-file "quipper_test_file.txt")
(def quip "If at first you don't succeed... so much for skydiving.")
(def mquip (str "First the doctor told me the good news: \n"
                "I was going to have a disease named after me."))
(def emquip (file/escape-newlines mquip))

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
  (file/add-quip test-file quip)
  (is (= (slurp test-file) (str/join [quip "\n"]))))

(deftest add-2-quips
  []
  (dotimes [n 2] (file/add-quip test-file quip))
  (is (= (slurp test-file) (str/join [quip "\n" quip "\n"]))))

(deftest add-multiline-quip
  []
  (file/add-quip test-file mquip)
  (is (= (slurp test-file) (str/join [emquip "\n"]))))

(deftest add-2-multiline-quips
  []
  (dotimes [n 2] (file/add-quip test-file mquip))
  (is (= (slurp test-file) (str/join [emquip "\n" emquip "\n"]))))

(deftest get-quip
  []
  (file/add-quip test-file quip)
  (is (= (file/get-quip test-file) quip)))

(deftest get-all-quips
  []
  (spit test-file (str/join [emquip "\n" emquip "\n"]))
  (is (= (file/all-quips test-file) [mquip mquip])))

(deftest get-multiline-quip
  []
  (file/add-quip test-file mquip)
  (is (= (file/get-quip test-file) mquip)))

(deftest drop-quips
  []
  (spit test-file "there is stuff here")
  (file/drop-quips test-file)
  (is (= (io/delete-file test-file :silent true) :silent)))

(deftest count-quips
  []
  (dotimes [n 42] (file/add-quip test-file mquip))
  (is (= (file/count-quips test-file) 42)))

;;(run-all-tests)

