(ns dwace.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [dwace.core-test]))

(doo-tests 'dwace.core-test)

