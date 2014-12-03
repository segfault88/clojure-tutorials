(ns tutorial1.consumers.web-test
    (:require [clojure.test :refer :all]
              [schema.test :as st]
              [clj-http.client :as http]
              [cheshire.core :refer [generate-string]]
              [tutorial1.consumers.web :as web]))

;; setup server

(def test-file "quipper_test_file.txt")
(def test-port 4444)
(def url (format "http://localhost:%s/quips" test-port))

(defn rest-fixture [f]
  (with-open [_ (web/start test-port test-file)]
    (f)))

(use-fixtures :once st/validate-schemas)
(use-fixtures :each rest-fixture)

;; helper functions

(defn quips [& quips]
  {:quips (map (fn [quip] {:quip quip}) quips)})

(def default-options
  {:as :json
   :throw-exceptions false})

(defn add-quips [& args]
  (http/post url (merge default-options
                        {:body (generate-string (apply quips args))})))

(defn random-quip []
  (http/get (str url "/random") default-options))

(defn drop-quips []
  (http/delete url default-options))

(defn count-quips []
  (http/get (str url "/count") default-options))

;; constants

(def quip "If at first you don't succeed... so much for skydiving.")
(def mquip (str "First the doctor told me the good news: \n"
                "I was going to have a disease named after me."))

;; tests

(testing "Should be able to add a quip"
  (is (= (add-quips quip) {:status 201 :body (quips quip)})))

(testing "Should be able to get the quip"
  (is (= (add-quips quip) {:status 201 :body (quips quip)}))
  (is (= (random-quip) {:status 200 :body {:quip quip}})))

(testing "Should be able to drop all existing quips"
  (is (= (add-quips quip) {:status 201 :body (quips quip)}))
  (is (= (drop-quips) {:status 204}))
  (is (= (random-quip) {:status 200 :body {}})))

(testing "Should be able to add a multiline quip"
  (is (= (add-quips mquip) {:status 201 :body (quips mquip)}))
  (is (= (random-quip) {:status 200 :body mquip})))

(testing "Should be able to get all quips in store"
  (let [all-quips #{quips mquip}]
    (is (= (apply add-quips all-quips)
           {:status 201 :body (apply quips all-quips)}))
    (let [seen-quips (loop [seen-quips #{}
                            counter 1000]
                       (if (or (zero? counter) (= all-quips seen-quips))
                         seen-quips
                         (recur (conj seen-quips (random-quip)) (dec counter))))]
      (is (= seen-quips all-quips)))))

(testing "Should be able to get a quip count"
  (is (= (add-quips quip mquip "Third quip")
         {:status 200 :body (quips quip mquip "Third quip")}))
  (is (= (count-quips)
         {:status 200 :body {:count 3}})))

(run-all-tests)
