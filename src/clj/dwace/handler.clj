(ns dwace.handler
  (:require [dwace.layout :refer [error-page]]
            [dwace.routes.home :refer [home-routes]]
            [dwace.routes.websockets :refer [websocket-routes]]
            [dwace.env :refer [defaults]]
            [dwace.middleware :as middleware]
            [compojure.route :as route]
            [compojure.core :refer [routes wrap-routes]]
            [mount.core :as mount]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes #'websocket-routes
          (-> #'home-routes
              (wrap-routes middleware/wrap-csrf)
              (wrap-routes middleware/wrap-formats))
          (route/not-found
           (:body
            (error-page {:status 404
                         :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
