(ns web-repl.views.compile
  (:use [noir.core :only [defpage]]
        [noir.response :only [content-type]]
        [chlorine.js])
  (:require [noir.cookies :as cookies])
  (:import java.util.Date))

(def ^{:doc "Stores data (temp-sym-count, last-sexpr and macros) of sessions"}
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

(defn start-new-session
  "Prepares starting vars for a new session"
  []
  {:temp-sym-count (ref 999)
   :last-sexpr (ref nil)
   :timestamp (now)
   :macros (ref {})})

(defn gen-session-id
  "Generates a new session id (an UUID one).
If maximum number of sessions exceeds, returns nil"
  []
  (when (< (count @sessions) max-session-number)
    (str (java.util.UUID/randomUUID))))

(defn new-session
  "Prepares a new session add adds it to global ref `sessions`.
Returns nil if gen-session-id returns nil
 (which means maximum number of sessions exceeds)"
  []
  (when-let [session-id (gen-session-id)]
    (dosync (commute sessions #(assoc % session-id (start-new-session))))
    (cookies/put! :compile-session-id session-id)))

(defn kill-session
  "Kill a session in case it's too old"
  [session-id] ;; TODO: delete all child refs
  (dosync (commute sessions #(dissoc % session-id))))

(defn compilation
  "Compiles a Chlorine expression (already read by Clojure reader, not string)
to Javascript string."
  [session-id expr]
  (let [session (get @sessions session-id)]
    (binding [*temp-sym-count* (:temp-sym-count session)
              *last-sexpr*      (:last-sexpr session)
              *macros*          (:macros session)
              *print-pretty*   true]
      (with-out-str
        (try
          (js-emit expr)
          (catch Throwable e
            (println e)))))))

(defpage [:POST "/compile"] {:keys [command]}
  (if-let [session-id (or (when-let [id (cookies/get :compile-session-id)]
                                (and (get @sessions id)
                                id))
                             (new-session))]
    (content-type "text/plain"
        {:status 200
         :body (compilation session-id (read-string command))})
    (content-type "text/plain"
        {:status 503
         :body "Server busy"})))
