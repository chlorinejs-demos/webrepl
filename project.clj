(defproject cl2-web-repl "0.2.0-SNAPSHOT"
  :description "Chlorine REPL on the Web."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [lib-noir "0.3.5"]
                 [chlorine "1.7.0-SNAPSHOT"]
                 [core-cl2 "1.0.0-SNAPSHOT"]]
  :plugins [[lein-ring "0.8.2"]]
  :ring {:handler web-repl.server/handler
         }
  :aot :all
  :main web-repl.server)
