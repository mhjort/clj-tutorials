(ns clj-tutorials.load-testing
  (:require [org.httpkit.client :as http]
            [clojure.core.async :as async :refer [go go-loop alts! alts!! put! <!! <! >!]]))










;;; Load testing using core.async (@mhjort) ;;;





































(def ping-scenario
  {:name "Ping scenario"
   :requests [{:name "Ping endpoint" :http "http://localhost:8080/ping"}]})

(def options
  {:requests 200 :timeout-in-ms 90})




























;STEP 1
(defn run1 [concurrency step]
  (let [cs (repeatedly concurrency async/chan)]
    (doseq [c cs]
      (go (>! c (step))))))

;; "go transforms your sequential code into a state machine
;;  way more complicated than you could ever write yourself."
;;
;;  -- LispCast


































;STEP 2
(defn run-with-results [concurrency step]
  (let [cs (repeatedly concurrency async/chan)]
    (doseq [c cs]
      (go (>! c (step))))
    (repeatedly concurrency #(first (alts!! cs)))))































;STEP 3
(defn bench [function]
  (let [start (System/currentTimeMillis)
        result (function)]
    [(- (System/currentTimeMillis) start) result]))

(defn run-with-bench [concurrency step]
  (let [cs (repeatedly concurrency async/chan)]
    (doseq [c cs]
      (go (>! c (bench step))))
    (repeatedly concurrency #(first (alts!! cs)))))























;STEP 4
(defn http-get [url]
  (fn []
    (= 200 (:status @(http/get url)))))

(defn run-with-url [concurrency url]
  (let [cs (repeatedly concurrency async/chan)]
    (doseq [c cs]
      (go (>! c (bench (http-get url)))))
    (repeatedly concurrency #(first (alts!! cs)))))


























;(STEP 5 "Non blocking http")


(defn async-http-get [url c]
  (let [now   #(System/currentTimeMillis)
        start (now)]
    (go
      (http/get url {} #(put! c [(- (now) start)
                                 (= 200 (:status %))])))))

(defn run-non-blocking [concurrency url]
  (let [cs (repeatedly concurrency async/chan)]
    (doseq [c cs]
      (go (async-http-get url c)))
    (repeatedly concurrency #(first (alts!! cs)))))





















; (STEP 6 "With timeout")

(defn async-http-get-with-timeout [url timeout result-channel]
  (let [now      #(System/currentTimeMillis)
        start    (now)
        response (async/chan)]
    (go
      (http/get url {} #(put! response [(- (now) start) (= 200 (:status %))]))
      (let [[result c] (alts! [response (async/timeout timeout)])]
        (if (= c response)
          (>! result-channel result)
          (>! result-channel [timeout false]))))))

(defn run-non-blocking-with-timeout [concurrency timeout url]
  (let [cs (repeatedly concurrency async/chan)]
    (doseq [c cs]
        (go (async-http-get-with-timeout url timeout c)))
    (repeatedly concurrency #(first (alts!! cs)))))


























; (STEP 7 "Constantly")

(defn run-constantly [concurrency number-of-requests timeout url]
  (let [cs       (repeatedly concurrency async/chan)
        results  (async/chan)]
    (doseq [c cs]
        (async-http-get-with-timeout url timeout c))
    (go-loop [i 0]
      (let [[result c] (alts! cs)]
        (when (< i (- number-of-requests concurrency))
          (async-http-get-with-timeout url timeout c))
        (>! results result)
        (when (<= i number-of-requests)
          (recur (inc i)))))
    (repeatedly number-of-requests #(<!! results))))













