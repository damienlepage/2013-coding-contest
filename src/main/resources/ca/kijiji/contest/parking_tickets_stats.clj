(ns ca.kijiji.contest.parking-tickets-stats
  (:use [clojure.java.io :only [reader]]
        [clojure.string :only [split]])
  (:import java.util.TreeMap))

(def batch-size 1000)

(defn parse-name [address]
  (let [direction-match (re-matches #"^(.*) (E|W|EAST|WEST|N|S)$" address)
        no-direction (if direction-match (second direction-match) address)
        name-match (re-matches #"^.*? (.*) .*$" no-direction)
        name (if name-match (second name-match) nil)]
    name))

(defn parse-name-amount [line]
  (let [fields (split line #",")
        amount (Integer/parseInt (nth fields 4))
        name (parse-name (nth fields 7))]
    [name amount]))

(defn merge-amount [streets [name amount]]
  (if name
    (let [current-sum (get streets name 0)]
      (assoc streets name (int (+ current-sum amount))))
    streets))

(defn parse-batch [lines]
  (reduce
    merge-amount
    {}
    (map parse-name-amount lines)))

(defn parse-streets [stream]
  (with-open [rdr (reader stream)]
    (.readLine rdr) ; skip header
    (apply
      merge-with
      (comp int +)
      (pmap parse-batch (partition-all batch-size (line-seq rdr))))))

(defn comparator-by-value-desc [m]
  (fn [k1 k2]
    (let [v1 (m k1), v2 (m k2)]
      (if (= v1 v2)
        (compare k1 k2)
        (compare v2 v1)))))

(defn streets-by-profitability [stream]
  (let [streets (parse-streets stream)
        sortedStreets (TreeMap. (comparator-by-value-desc streets))]
    (doto sortedStreets (.putAll streets))))