(ns dwace.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [dwace.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[dwace started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[dwace has shutdown successfully]=-"))
   :middleware wrap-dev})
