(ns web-repl.views.compile
  (:use [compojure.core :refer [defroutes GET POST]]
        [noir.response :only [redirect content-type]]
        [clojure.stacktrace :only [print-cause-trace]]
        [slingshot.slingshot]
        [chlorine.js])
  (:require [noir.cookies :as cookies])
  (:import java.util.Date))

(def ^{:doc "Stores data (temp-sym-count and macros) of sessions"}
  sessions (ref {}))

(defn now "Gets current time in miliseconds"
  [] (.getTime (Date.)))

(def ^{:doc "Maximum time a session is kept"}
  max-session-age
  (* 1 30 60 1000)) ;; Default: 30 minutes

(def ^{:doc "Maximum number of working session at a time"}
  max-session-number 20)

(defn too-old?
  "Checks if a session is too old (aka longer than maximum age)"
  [session-id]
  (let [age (- (now) (:timestamp (get @sessions session-id)))]
    (< max-session-age age)))

(def ^{:doc "Pre-compiles Chlorine `dev` strategy once
and saves states to this var."}
  preloaded
  (binding [*temp-sym-count* (ref 999)
            *macros*         (ref {})
            *print-pretty*   true]
    (let [core-js (js (load-file "r:/strategies/dev.cl2"))]
      {:temp-sym-count @*temp-sym-count*
       :macros @*macros*
       :core-js core-js})))

(defn start-new-session
  "Prepares starting vars for a new session"
  []
  {:temp-sym-count (ref (:temp-sym-count preloaded))
   :macros         (ref (:macros         preloaded))
   :timestamp (now)})

(defn gen-session-id
  "Generates a new session id (an UUID one).
If maximum number of sessions exceeds, returns nil"
  []
  (when (< (count @sessions) max-session-number)
    (str (java.util.UUID/randomUUID))))

(defn kill-session
  "Kills a session in case it's too old"
  [session-id]
  (dosync (commute sessions #(dissoc % session-id))))

(defn kill-all
  "Finds and kills all sessions that are too old."
  []
  (map kill-session
   (filter too-old? (keys @sessions))))

(defn new-session
  "Prepares a new session add adds it to global ref `sessions`.
Returns nil if gen-session-id returns nil
 (which means maximum number of sessions exceeds)"
  []
  (try+
    (kill-all)
    (when-let [session-id (gen-session-id)]
      (dosync (commute sessions #(assoc % session-id (start-new-session))))
      (cookies/put! :compile-session-id session-id))
    (catch Throwable e
      (println "new-session suck!!!")
      (print-cause-trace e 10))))

(defn compilation
  "Compiles a Chlorine expression (already read by Clojure reader, not string)
to Javascript string."
  [session-id expr]
  (let [session (get @sessions session-id)]
    (binding [*temp-sym-count* (:temp-sym-count session)
              *macros*          (:macros session)
              *print-pretty*   true]
      (with-out-str
        (try+
         (js-emit expr)
         (catch map? e
           (js-emit
            (with-out-str
              (println "Compilation Error: ")
              (println (:msg e))
              (doseq [i (range (count (:causes e)))
                      :let [cause (nth (:causes e) i)]]
                (print (apply str (repeat (inc i) "  ")))
                (println "caused by " cause))
              (when-let [trace (:trace e)]
                (print-cause-trace trace 3)))))
         (catch Throwable e
           (js-emit
            (with-out-str (print-cause-trace e 3)))))))))

(defroutes compiling
  ;; test from CLI with this:
  ;; curl -X POST http://localhost:3000/compile -d command='(def x 1)'

  (POST "/compile" [command]
        (if-let [session-id (or (when-let [id (cookies/get :compile-session-id)]
                                  (and (get @sessions id)
                                       id))
                                (new-session))]
          (content-type "text/plain"
                        {:status 200
                         :body (let [expr (read-string command)]
                                 (if (map? expr)
                                   (format "(%s)" (compilation session-id expr))
                                   (compilation session-id expr)))})
          (content-type "text/plain"
                        {:status 503
                         :body "Server busy"})))
  (GET "/core-cl2.js" []
       (:core-js preloaded))
  (GET "/" []
       (redirect "/index.html"))
  )
