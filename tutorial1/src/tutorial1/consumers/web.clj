(ns tutorial1.consumers.web
  (:require [schema.core :as s]
            [org.httpkit.server :as server]
            [compojure
             [core :refer :all]
             [handler :as handler]
             [route :as route]]
            [ring.middleware.json :as json]
            [tutorial1.producers.producer :as producer]
            [tutorial1.producers.transit :as transit]
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
  [producer req]
  (let [quips (-> req :body :quips)]
    (doseq [quip quips]
      (producer/add-quip producer (:quip quip)))
    (json-response {:quips quips} 201)))

(defn random-route
  [producer]
  (json-response
   (if-let [quip (producer/get-quip producer)]
     {:quip quip}
     {})))

(defn count-route
  [producer]
  (json-response
   {:count (producer/count-quips producer)}))

(defn delete-route
  [producer]
  (producer/drop-quips producer)
  {:status 204})

(defn api-routes [producer]
  (routes
   (context "/quips" []
            (POST "/" [:as req] (add-quip-route producer req))
            (GET "/count" [] (count-route producer))
            (GET "/random" [] (random-route producer))
            (DELETE "/" [] (delete-route producer))
            (route/not-found
             "<h1>This is not the page you're looking for... Move along...</h1>"))))

(defn app [producer]
  (-> (api-routes producer)
      handler/api
      (json/wrap-json-body {:keywords? true})
      gulp-errors
      json/wrap-json-response))

(s/defn start
  [port :- s/Int
   file :- s/Str
   producer-name :- s/Str]
  (let [producer (case producer-name
                   "transit" (tutorial1.producers.transit.TransitProducer. file)
                   "file" (tutorial1.producers.file.FileProducer. file)
                   :else (throw (Exception. "Invalid consumer!")))]
    (server/run-server (app producer) {:port port})))

