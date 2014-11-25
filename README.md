clj-tutorials
=============

## Load testing using core.async @ ClojuTre 2014

### Test server

You should start this before (propably in own Repl)

```clojure

(require '[clj-tutorials.main :as main])

(main/run 1000)

```

### "Slides"


Run in Repl and check steps from clj file.


```clojure

(require '[clj-tutorials.load-testing :refer :all])

(run-simulation [ping-scenario] 100 options)

(run1 10 #(do (println "hi") true))

(run-with-results 10 #(do (println "hi") true))

(run-with-bench 10 #(do (println "hi") true))

(run-with-url 100 "http://localhost:8080/ping")

(run-non-blocking 1000 "http://localhost:8080/ping")

(run-non-blocking-with-timeout 1000 90 "http://localhost:8080/ping")

(run-constantly 100 5000 90 "http://localhost:8080/ping")

```


### Test server

```clojure

(require '[clj-tutorials.main :as main])

(main/run 1000)

```

