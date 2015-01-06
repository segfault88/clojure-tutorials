(ns tutorial1.producers.file
  (:require [schema.core :as s]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn file-exists?
  [file]
  (.exists (io/as-file file)))

(defn qslurp
  [file]
  (if (file-exists? file)
    (slurp file)
    ""))

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
  (if (file-exists? file)
    (unescape-newlines (rand-nth (str/split (qslurp file) #"\n")))
    nil))

(defn all-quips
  [file]
  (map #(unescape-newlines %) (str/split (qslurp file) #"\n")))

(defn count-quips
  [file]
  (count (str/split (qslurp file) #"\n")))

(defn drop-quips
  [file]
  (if (file-exists? file)
    (io/delete-file file true)))
