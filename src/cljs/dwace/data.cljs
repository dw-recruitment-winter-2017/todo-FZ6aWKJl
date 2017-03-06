(ns dwace.data
  (:require [datascript.core :as d]
            [posh.reagent :as p]))


(defonce conn (d/create-conn))
