(ns clj-tutorials.core
  (:require [clojure.core.async :as async :refer [go <! >!]]))

(defn run1 [number-of-users step]
  (let [cs (repeatedly number-of-users async/chan)]
    (doseq [c cs]
      (go (>! c (step))))))


(defn- collect-result [cs]
  (let [[result c] (async/alts!! cs)]
    result))

(defn run-with-results [number-of-users step]
  (let [cs (repeatedly number-of-users async/chan)]
    (doseq [c cs]
      (go (>! c (step))))
    (repeatedly number-of-users #(collect-result cs))))



(defn run-with-close-and-results [number-of-users step]
  (let [cs (repeatedly number-of-users async/chan)]
    (doseq [c cs]
      (go (>! c (step))))
    (let [results (repeatedly number-of-users #(collect-result cs))]
      (doseq [c cs]
        (async/close! c))
      results)))



(defn run-with-close-and-results2 [number-of-users step]
  (let [cs (repeatedly number-of-users async/chan)]
    (doseq [c cs]
      (go (>! c (step))))
    (let [results (repeatedly number-of-users #(collect-result cs))]
      (dorun results)
      (doseq [c cs]
        (async/close! c))
      results)))
