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


(defmacro with-channels [binding & body]
  `(let [~(first binding) (repeatedly ~(second binding) async/chan)
    ~'result (do ~@body)]
    (dorun ~'result) ;Lazy results must be evaluated before channels are closed
    (doseq [~'c ~(first binding)] (async/close! ~'c))
  ~'result))

(defn run-with-macro [number-of-users step]
  (with-channels [cs number-of-users]
    (doseq [c cs]
      (go (>! c (step))))
    (repeatedly number-of-users #(collect-result cs))))

(defn bench [function]
  (let [start (System/currentTimeMillis)
        result (function)]
    [(- (System/currentTimeMillis) start) result]))

(defn run-with-bench [number-of-users step]
  (with-channels [cs number-of-users]
    (doseq [c cs]
      (go (>! c (bench step))))
    (repeatedly number-of-users #(collect-result cs))))



(defn callback->chan [fn-with-cb]
  (let [start (System/currentTimeMillis)
        c (async/chan)]
    (fn-with-cb #(async/put! c [(- (System/currentTimeMillis) start) %]))
    c))

(defn run-non-blocking [number-of-users step]
  (let [cs (repeatedly number-of-users async/chan)]
    (doseq [c cs]
      (go (>! c (callback->chan step))))
    (let [results (repeatedly number-of-users #(collect-result cs))]
      (repeatedly number-of-users  #(collect-result results)))))
