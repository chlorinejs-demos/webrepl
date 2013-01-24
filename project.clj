(defproject cl2-web-repl "0.2.0-SNAPSHOT"
            :description "Chlorine REPL on the Web."
            :dependencies [[org.clojure/clojure "1.4.0"]
                            [noir "1.3.0-beta3"]
                            [chlorine "1.7.0-SNAPSHOT"]
                            [core-cl2 "1.0.0-SNAPSHOT"]]
            :main web-repl.server)

