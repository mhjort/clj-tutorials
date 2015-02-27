(ns app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [ajax.core :refer [GET]]
            [domina :refer [by-id set-text!]]
            [domina.css :refer [sel]]
            [domina.events :refer [listen!]]
            [cljs.core.async :as async :refer [<! >!]]
            [lively.core :as lively]))

;(lively/start "/js/app.js" {:on-reload #(.log js/console "Reloaded!")})

(def c (async/chan))

(defn set-result! [result]
  (set-text! (by-id "result") result))

(defn ping []
  (GET "http://localhost:8080/ping" {:handler #(go (>! c %))}))

(listen! (sel "button") :click #(ping))

(go
  (while true
    (let [result (<! c)]
      (set-result! result))))
