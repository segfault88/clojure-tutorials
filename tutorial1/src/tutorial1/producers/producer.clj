(ns tutorial1.producers)

(defprotocol Producer
  "A basic quip producer"
  (add-quip [this quip])
  (get-quip [this])
  (all-quips [this])
  (count-quips [this])
  (drop-quips [this]))
