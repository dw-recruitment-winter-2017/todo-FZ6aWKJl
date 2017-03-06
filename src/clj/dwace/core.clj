(ns dwace.core
  (:require [dwace.handler :as handler]
            [dwace.repl :as repl]
            [dwace.config :refer [env]]
            [dwace.logger :as logger]
            [org.httpkit.server :as http]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.logging :as log]
            [mount.core :as mount])
  (:gen-class))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])


(defn start-http [{:keys [handler host port] :as opts}]
  (try
    (log/info "starting HTTP server on port" port)
    (http/run-server handler (dissoc opts :handler :init))
    (catch Throwable t
      (log/error t (str "server failed to start on" host "port" port))
      (throw t))))

(defn stop-http [http-server]
  (http-server :timeout 100)
  (log/info "HTTP server stopped"))


(mount/defstate ^{:on-reload :noop}
                http-server
                :start
                (start-http
                  (-> env
                      (assoc :handler (handler/app))
                      (update :port #(or (-> env :options :port) %))))
                :stop
                (stop-http http-server))

(mount/defstate ^{:on-reload :noop}
                repl-server
                :start
                (when-let [nrepl-port (env :nrepl-port)]
                  (repl/start {:port nrepl-port}))
                :stop
                (when repl-server
                  (repl/stop repl-server)))

(mount/defstate log
                :start (logger/init (:log-config env)))


(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (start-app args))
