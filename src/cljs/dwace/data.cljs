(ns dwace.data
  (:require [reagent.core :as r]
            [datascript.core :as d]
            [posh.reagent :as p]
            [dwace.websockets :as ws]))


(defonce conn (d/create-conn))

(defonce init-posh (p/posh! conn))

(defonce counter (r/atom 0))


(defn insert-todo [item]
  (let [key (swap! counter inc)
        todo [{:todo/key key
               :todo/item item
               :todo/completed false}]]
    (p/transact! conn todo)
    (ws/send-transit-msg! {:insert-todo todo})))


;; stub function for future handling of updates from server
(defn update-todo-list! [])


(defn get-todos []
  @(p/q '[:find ?e ?item
          :where [?e :todo/item ?item]]
        conn))

(defn get-todo-ids []
  @(p/q '[:find ?e 
          :where [?e :todo/item _]]
        conn))

(defn pull-todo [id]
  (let [t @(p/pull conn '[*] id)]
    t))

(defn pull-todos []
  (for [d (get-todo-ids)]
    (pull-todo (first d))))

(defn todo-status-change
  [id]
  (let [t @(p/pull conn '[*] id)]
    (p/transact! conn [[:db/add id :todo/completed (not (:todo/completed t))]])
    (ws/send-transit-msg! {:update-todo @(p/pull conn '[*] id)})))


(defn delete-todo
  [id]
  (let [t @(p/pull conn '[*] id)]
    (p/transact! conn [[:db.fn/retractEntity id]])
    (ws/send-transit-msg! {:remove-todo t})))
