(ns dwace.db.core
  (:require [dwace.config :refer [env]]
            [datomic.api :as d]
            [mount.core :refer [defstate]]))

(defn create-connection []
  (-> env :database-url d/create-database)
  (-> env :database-url d/connect))

(defn disconnect [conn]
  (-> conn .release))

(defstate conn
  :start (create-connection)
  :stop (disconnect conn))


