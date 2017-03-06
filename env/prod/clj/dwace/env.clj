(ns dwace.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[dwace started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[dwace has shutdown successfully]=-"))
   :middleware identity})
