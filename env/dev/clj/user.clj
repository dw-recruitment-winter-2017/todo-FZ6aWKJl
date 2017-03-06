(ns user
  (:require [mount.core :as mount]
            [dwace.figwheel :refer [start-fw stop-fw cljs]]
            dwace.core))

(defn start []
  (mount/start-without #'dwace.core/repl-server))

(defn stop []
  (mount/stop-except #'dwace.core/repl-server))

(defn restart []
  (stop)
  (start))


