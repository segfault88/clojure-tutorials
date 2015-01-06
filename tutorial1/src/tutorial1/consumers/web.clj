(ns tutorial1.consumers.web
  (:require [schema.core :as s]
            [org.httpkit.server :as server]
            [compojure
             [core :refer :all]
             [handler :as handler]
             [route :as route]]
            [ring.middleware.json :as json]
            [tutorial1.producers.file :as file])
  (:import com.fasterxml.jackson.core.JsonGenerationException))

(defn gulp-errors
  [handler]
  (fn [req]
    (try
      (handler req)
      (catch JsonGenerationException e
        {:status 500 :body {:error "Unknown error occurred"}})
      (catch Exception e
        {:status 500 :body {:error (str e)}}))))

(defn json-response
  ([data]
   (json-response data 200))
  ([data status]
   {:status status
    :body data}))

(defn add-quip-route
  [file req]
  (let [quips (-> req :body :quips)]
    (doseq [quip quips]
      (file/add-quip file quip))
    (json-response {:quips quips} 201)))

(defn random-route
  [file]
  (json-response
   (if-let [quip (file/get-quip file)]
     {:quip quip}
     {})))

(defn count-route
  [file]
  (json-response
   {:count (file/count-quips file)}))

(defn delete-route
  [file]
  (file/drop-quips file)
  {:status 204})

(defn api-routes [file]
  (routes
   (context "/quips" []
            (POST "/" [:as req] (add-quip-route file req))
            (GET "/count" [] (count-route file))
            (GET "/random" [] (random-route file))
            (DELETE "/" [] (delete-route file))
            (route/not-found "<h1>This is not the page you're looking for... Move along...</h1>"))))

(defn app [file]
  (-> (api-routes file)
      handler/api
      (json/wrap-json-body {:keywords? true})
      gulp-errors
      json/wrap-json-response))

(s/defn start
  [port :- s/Int
   file :- s/Str]
  (server/run-server (app file) {:port port}))
