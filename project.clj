(defproject clj-tutorials "0.1.0-SNAPSHOT"
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [http-kit "2.1.16"]
                 [ring/ring-core "1.3.1"]
                 [javax.servlet/servlet-api "2.5"]
                 [compojure "1.2.1"]
                 [clj-gatling "0.4.4"]
                 [org.clojure/clojurescript "0.0-2913"]
                 [cljs-ajax "0.3.10"]
                 [domina "1.0.3"]
                 [lively "0.2.0"]]
  :plugins [[lein-cljsbuild "1.0.5"]]
  :cljsbuild {
    :builds [{
        :source-paths ["src/cljs"]
        :compiler {
          :output-to "resources/public/js/app.js"
          :output-dir "resources/public/js/out"
          :source-map true
          :optimizations :none
          :pretty-print true}}]})
