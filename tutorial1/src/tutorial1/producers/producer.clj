(ns tutorial1.producers)

(defprotocol Producer
  "A basic quip producer"
  (add-quip [this f quip])
  (get-quip [this f])
  (all-quips [this f])
  (count-quips [this f])
  (drop-quips [this f]))
