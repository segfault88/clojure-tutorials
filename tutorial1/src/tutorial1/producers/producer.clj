(ns tutorial1.producers.producer)

(defprotocol Producer
  "A basic quip producer"
  (add-quip [this quip])
  (get-quip [this])
  (all-quips [this])
  (count-quips [this])
  (drop-quips [this]))
