(ns tutorial1.producers.file
  (:require [schema.core :as s]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [cognitect.transit :as transit]
            [tutorial1.producers.producer :as producer]))

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

(deftype FileProducer [file]
  producer/Producer

  (add-quip
    [this quip]
    (spit file (format-quip quip) :append true))

  (get-quip
    [this]
    (if (file-exists? file)
      (unescape-newlines (rand-nth (str/split (qslurp file) #"\n")))
      nil))

  (all-quips
    [this]
    (map #(unescape-newlines %) (str/split (qslurp file) #"\n")))

  (count-quips
    [this]
    (count (str/split (qslurp file) #"\n")))

  (drop-quips
    [this]
    (if (file-exists? file)
      (io/delete-file file true))))
