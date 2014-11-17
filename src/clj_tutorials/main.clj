(ns clj-tutorials.main
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [org.httpkit.server :refer [run-server]]))

(def ongoing-requests (atom 0))

(defn- pong []
  (let [ongoing-reqs (swap! ongoing-requests inc)
        start (System/currentTimeMillis)]
    (when (= 0 (mod ongoing-reqs 10))
      (prn "Ongoing requests " ongoing-reqs))
    (Thread/sleep 50)
    (swap! ongoing-requests #(- % 1))
    "pong"))

(defroutes app-routes
  (GET "/ping" [] (pong)))

(defn run []
  (run-server (handler/api app-routes) {:port 8080 :join? false :thread 1000}))


