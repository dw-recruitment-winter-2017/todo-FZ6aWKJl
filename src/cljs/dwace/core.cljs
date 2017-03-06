(ns dwace.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [dwace.websockets :as ws])
  (:require-macros [reagent.ratom :refer [reaction]])
  (:import goog.History))


(defn add-todo []
  [:div
   [:h3 "Add ToDo"]])

(defn todo-list []
  [:div
   [:h3 "ToDo List"]])

(defn navbar []
  [:div
   [:a {:href "#/"} "Home"]
   [:br]
   [:a {:href "#/about"} "About"]])

(defn about-page []
  [:div.container
   [:h1 "About"]
   [:hr]
   [:p "This is a simple todo list manager written as a coding exercise."]
   [:p "Currently data is stored on the server only while the server is running."]])

(defn home-page []
  (fn []
    [:div
     [:h1 "Simple ToDo List Manager"]
     [:hr]
     [todo-list]
     [add-todo]]))

(def pages
  {:home #'home-page
   :about #'about-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/about" []
  (session/put! :page :about))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  ;(ws/make-websocket! (str "ws://" (.-host js/location) "/ws") update-messages!)
  (mount-components))
