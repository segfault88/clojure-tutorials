(ns tutorial1.producers.transit
  (:require [schema.core :as s]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [cognitect.transit :as transit]
            [tutorial1.producers.producer :as producer])
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
  (let [out (FileOutputStream. file false) ;; do not append, overwrite
        writer (transit/writer out :msgpack)]
    (transit/write writer {:quips quips})))

(deftype TransitProducer [file]
  producer/Producer

  (add-quip
    [this quip]
    (write-quips file (conj (read-quips file) quip)))
  
  (get-quip
    [this]
    (if (file-exists? file)
      (rand-nth (read-quips file))
      nil))
  
  (all-quips
    [this]
    (read-quips file))

  (count-quips
    [this]
    (count (read-quips file)))
  
  (drop-quips
    [this]
    (if (file-exists? file)
      (io/delete-file file true))))
