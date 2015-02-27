(ns app
  (:require [ajax.core :refer [GET]]
            [domina :refer [by-id set-text!]]
            [domina.css :refer [sel]]
            [domina.events :refer [listen!]]
            [lively.core :as lively]))

(lively/start "/js/app.js" {:on-reload #(.log js/console "Reloaded!")})

(defn set-result! [result]
  (set-text! (by-id "result") result))

(defn ping []
  (GET "http://localhost:8080/ping" {:handler #(set-result! %)}))

(listen! (sel "button") :click #(ping))
