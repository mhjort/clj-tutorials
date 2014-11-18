(ns clj-tutorials.core
  (:require [org.httpkit.client :as http]
            [clojure.core.async :as async :refer [go <! >!]]))



;STEP 1
(defn run1 [number-of-users step]
  (let [cs (repeatedly number-of-users async/chan)]
    (doseq [c cs]
      (go (>! c (step))))))



;STEP 2
(defn- collect-result [cs]
  (let [[result c] (async/alts!! cs)]
    result))

(defn run-with-results [number-of-users step]
  (let [cs (repeatedly number-of-users async/chan)]
    (doseq [c cs]
      (go (>! c (step))))
    (repeatedly number-of-users #(collect-result cs))))


;STEP 3
(defn bench [function]
  (let [start (System/currentTimeMillis)
        result (function)]
    [(- (System/currentTimeMillis) start) result]))

(defn run-with-bench [number-of-users step]
  (let [cs (repeatedly number-of-users async/chan)]
    (doseq [c cs]
      (go (>! c (bench step))))
    (repeatedly number-of-users #(collect-result cs))))



;STEP 4
(defn http-get [url]
  (fn []
    (= 200 (:status @(http/get url)))))

(defn run-with-url [number-of-users url]
  (let [cs (repeatedly number-of-users async/chan)]
    (doseq [c cs]
      (go (>! c (bench (http-get url)))))
    (repeatedly number-of-users #(collect-result cs))))




;(STEP 5 "Non blocking http")


(defn chan-http-get [url c]
  (let [start (System/currentTimeMillis)]
    (http/get url {} #(async/put! c [(- (System/currentTimeMillis) start)
                                     (= 200 (:status %))]))))

(defn run-non-blocking [number-of-users url]
  (let [cs (repeatedly number-of-users async/chan)]
    (doseq [c cs]
        (go (chan-http-get url c)))
    (repeatedly number-of-users #(collect-result cs))))
