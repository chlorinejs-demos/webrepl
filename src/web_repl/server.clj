(ns web-repl.server
  (:use [compojure.core :only [defroutes routes]]
        [compojure.route :only [not-found resources]]
        [noir.cookies :only [wrap-noir-cookies*]]
        [noir.util.middleware :only [wrap-strip-trailing-slash]]
        [compojure.handler :only [site]]
        [web-repl.views.compile :only [compiling]]))

(def handler
  (site (-> (routes compiling
                    (resources "/")
                    (not-found "404 - Not found"))
            (wrap-noir-cookies*)
            )))