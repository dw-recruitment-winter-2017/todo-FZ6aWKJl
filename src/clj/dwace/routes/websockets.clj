(ns dwace.routes.websockets
  (:require [clojure.string :as str]
            [cognitect.transit :as t]
            [compojure.core :refer [GET defroutes]]
            [org.httpkit.server :refer [send! with-channel on-close on-receive]]
            [clojure.tools.logging :as log]
            [dwace.db.core :as d])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))


(defonce channels (atom #{}))

(defn encode-transit [message]
  (let [out (ByteArrayOutputStream. 4096)
        writer (t/writer out :json)]
    (t/write writer message)
    (.toString out)))

(defn decode-transit [message]
  (let [in (ByteArrayInputStream. (.getBytes message))
        reader (t/reader in :json)]
    (t/read reader)))

(defn shout-message [message]
  (let [old-json (decode-transit message)
        new-json (assoc old-json :message (str/upper-case (:message old-json)))]
    (encode-transit new-json)))

(defn connect! [channel]
  (log/info "channel open")
  (swap! channels conj channel))

(defn disconnect! [channel status]
  (log/info "channel closed:" status)
  (swap! channels #(remove #{channel} %)))

(defn ws-handler [request]
  (with-channel request channel
    (connect! channel)
    (on-close channel (partial disconnect! channel))
    (on-receive channel #(d/update-database (decode-transit %)))))

(defroutes websocket-routes
  (GET "/ws" request (ws-handler request)))
