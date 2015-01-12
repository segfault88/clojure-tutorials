(ns tutorial1.producers.transit
  (:require [schema.core :as s]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [cognitect.transit :as transit])
  (:import [java.io FileInputStream FileOutputStream]))

(defn file-exists?
  [file]
  (.exists (io/as-file file)))

(defn read-quips
  [file]
  (if (file-exists? file)
    (let [in (FileInputStream. file)
          reader (transit/reader in :msgpack)]
      (:quips (transit/read reader)))
    []))

(defn write-quips
  [file quips]
  (let [out (FileOutputStream. file false)
        writer (transit/writer out :msgpack)]
    (transit/write writer {:quips quips})))

(defn add-quip
  [file quip]
  (write-quips file (conj (read-quips file) quip)))

(defn get-quip
  [file]
  (if (file-exists? file)
    (rand-nth (read-quips file))
    nil))

(defn all-quips
  [file]
  (read-quips file))

(defn count-quips
  [file]
  (count (read-quips file)))

(defn drop-quips
  [file]
  (if (file-exists? file)
    (io/delete-file file true)))
