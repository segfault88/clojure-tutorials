(ns tutorial1.core-test
    (:require [clojure.test :refer :all]
              [clojure.string :as str]
              [schema.test :as st]
              [tutorial1.core :as core]))

(use-fixtures :once st/validate-schemas)

(defn test-main
  [args]
  (->> args
       (apply core/-main)
       with-out-str
       str/trim-new-line))

(deftest help-is-shown
  ())

(run-all-tests)
