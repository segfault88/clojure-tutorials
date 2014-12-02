(ns tutorial1.consumers.web-test
    (:require [clojure.test :refer :all]
              [schema.test :as st]
              [tutorial1.consumers.web :as web]))

(use-fixtures :once st/validate-schemas)

(run-all-tests)
