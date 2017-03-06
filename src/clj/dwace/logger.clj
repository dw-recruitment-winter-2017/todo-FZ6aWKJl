(ns dwace.logger)


(defn init [opts]
   (when-let [config (:log-config opts)]
     (org.apache.log4j.PropertyConfigurator/configure config)))
