(ns tutorial1.producers)

(defprotocol Producer
  "A basic quip producer"
  (add-quip [file quip])
  (get-quip [file])
  (all-quips [file])
  (count-quips [file])
  (drop-quips [file]))
