(ns tutorial1.producers.file
  (:require [schema.core :as s]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn escape-newlines
  [quip]
  (str/replace quip #"[\n]+" "\\\\n"))

(defn unescape-newlines
  [quip]
  (str/replace quip #"\\n" "\n"))

(defn format-quip
  [quip]
  (let [quip (escape-newlines quip)]
    (str/join [quip "\n"])))

(defn add-quip
  [file quip]
  (spit file (format-quip quip) :append true))

(defn get-quip
  [file]
  (unescape-newlines (rand-nth (str/split (slurp file) #"\n"))))

(defn all-quips
  [file]
  (map #(unescape-newlines %) (str/split (slurp file) #"\n")))

(defn count-quips
  [file]
  (count (str/split (slurp file) #"\n")))

(defn drop-quips
  [file]
  (io/delete-file file :silent true))
