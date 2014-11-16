(ns clj-tutorials.main
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [org.httpkit.server :refer [run-server]]))

(defroutes app-routes
  (GET "/ping" [] "pong"))

(defn run []
  (run-server (handler/api app-routes) {:port 8080 :join? false}))


