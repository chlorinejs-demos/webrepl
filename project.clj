(defproject cl2-web-repl "0.3.3"
  :description "Chlorine REPL on the Web."
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [lib-noir "0.3.5"]
                 [chlorine "1.5.3.1"]
                 [core-cl2 "0.7.2"]]
  :plugins [[lein-ring "0.8.2"]]
  :ring {:handler web-repl.server/handler
         }
  :aot :all
  :main web-repl.server)
