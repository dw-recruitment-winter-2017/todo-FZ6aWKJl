(ns dwace.db.core
  (:require [dwace.config :refer [env]]
            [datomic.api :as d]
            [mount.core :refer [defstate]]
            [clojure.tools.logging :as log]))

(defn create-connection []
  (-> env :database-url d/create-database)
  (-> env :database-url d/connect))

(defn disconnect [conn]
  (-> conn .release))

(defstate conn
  :start (create-connection)
  :stop (disconnect conn))


(def schema [{:db/id #db/id[:db.part/db]
              :db/ident :todo/key
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity
              :db/doc "todo key"
              :db.install/_attribute :db.part/db}
             
             {:db/id #db/id[:db.part/db]
              :db/ident :todo/item
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "todo item"
              :db.install/_attribute :db.part/db}

             {:db/id #db/id[:db.part/db]
              :db/ident :todo/completed
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one
              :db/doc "todo comppleted boolean"
              :db.install/_attribute :db.part/db}])


(defn load-schema []
  @(d/transact conn schema))

(defn add-todo [transaction]
  @(d/transact conn transaction))

(defn update-todo [transaction]
  (let [key (:todo/key transaction)
        completed (:todo/completed transaction)]
    @(d/transact conn [{:db/id [:todo/key key] :todo/completed completed}])))

(defn remove-todo [transaction]
  (let [key (:todo/key transaction)]
    @(d/transact conn [[:db.fn/retractEntity [:todo/key key]]])))

(defn update-database [transaction]
  (log/info transaction)
  (cond
    (:insert-todo transaction) (add-todo (:insert-todo transaction))
    (:update-todo transaction) (update-todo (:update-todo transaction))
    (:remove-todo transaction) (remove-todo (:remove-todo transaction))
    :else (log/info "error in database transaction message" transaction)))

(defn get-todo-list []
  (d/q '[:find ?k ?t ?c
         :where
         [?i :todo/key ?k]
         [?i :todo/item ?t]
         [?i :todo/completed ?c]]
       (d/db conn)))
