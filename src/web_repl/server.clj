(ns web-repl.server
  (:use [ring.adapter.jetty :only [run-jetty]]
        [compojure.core :only [defroutes routes]]
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
(defn -main []
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
    (run-jetty handler {:port port})))