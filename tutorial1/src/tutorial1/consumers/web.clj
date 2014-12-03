(ns tutorial1.consumers.web
  (:require [schema.core :as s]
            [aleph.http :as http]
            [compojure
             [core :refer :all]
             [handler :as handler]
             [route :as route]]
            [ring.middleware.json :as json]
            [tutorial1.producers.file :as file])
  (:import [com.fasterxml.jackson.core JsonGenerationException JsonParseException]))

(defn gulp-errors
  [handler]
  (fn [req]
    (try
      (handler req)
      (catch JsonGenerationException e
        {:status 500 :body {:error "Unknown error occurred"}})
      (catch JsonParseException e
        {:status 400 :body {:error "Malformed JSON"}})
      (catch Exception e
        {:status 500 :body {:error (str e)}}))))

(s/defn api-routes
  [file :- s/Str]
  (context "/quips" []
    (routes
      (GET "/random" []
        ;; if there are quips
        #_ {:status 200
            :body {:quip "<<Random Quip Goes here>>"}}
        ;; if there are no quips
        #_ {:status 200
            :body {}})
      (GET "/count" []
        #_ {:status 200
            :body {:count -1}})
      (POST "/" [:as req]
        (let [quips (get-in req [:body :quips])]
          #_ {:status 201
              :body {:quips [quips]}}))
      (DELETE "/" []
        {:status 204})
      (route/not-found nil))))

(s/defn app
  [file :- s/Str]
  (-> api-routes
      handler/api
      (json/wrap-json-body {:keywords? true})
      json/wrap-json-response
      gulp-errors))

(s/defn start
  [port :- s/Int
   file :- s/Str]
  (http/start-server (app file) {:port port}))
