(defproject cl2-web-repl "0.3.5"
  :description "Chlorine REPL on the Web."
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [lib-noir "0.6.1"]
                 [chlorine "1.6.1"]
                 [core-cl2 "0.8.0"]]
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler web-repl.server/handler
         }
  :aot :all
  :main web-repl.server)
