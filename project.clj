(defproject dwace "0.1.0"

  :description "Simple Todo List Manager"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[log4j-edn-config "0.1.1"]
                 [org.slf4j/slf4j-log4j12 "1.7.22"]
                 [org.apache.logging.log4j/log4j-core "2.7"]
                 [metosin/compojure-api "1.1.9"]
                 [cljs-ajax "0.5.8"]
                 [secretary "1.2.3"]
                 [reagent-utils "0.2.0"]
                 [reagent "0.6.0"]
                 [org.clojure/clojurescript "1.9.293" :scope "provided"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.395" :exclusions [org.clojure/tools.reader]]
                 [selmer "1.10.2"]
                 [markdown-clj "0.9.91"]
                 [ring-middleware-format "0.7.0"]
                 [metosin/ring-http-response "0.8.0"]
                 [bouncer "1.0.0"]
                 [org.webjars/pure "2.83"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [compojure "1.5.1"]
                 [ring-webjars "0.1.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-core "1.5.0"]
                 [expiring-map "0.1.7"]
                 [mount "0.1.11"]
                 [cprop "0.1.9"]
                 [org.clojure/tools.cli "0.3.5"]
                 [buddy "1.2.0"]
                 [datascript "0.15.5"]
                 [posh "0.5.5"]
                 [com.datomic/datomic-free "0.9.5544" :exclusions [com.google.guava/guava
                                                                   org.slf4j/log4j-over-slf4j
                                                                   org.slf4j/slf4j-nop]]
                 [http-kit "2.2.0"]]

  :min-lein-version "2.6.1"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]

  :main dwace.core

  :plugins [[lein-cprop "1.0.1"]
            [lein-cljsbuild "1.1.3"]]
  
  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :app :compiler :output-dir]
                                    [:cljsbuild :builds :app :compiler :output-to]]

  :cljsbuild {:builds {:app {:source-paths ["src/cljc" "src/cljs"]
                             :compiler {:output-to "target/cljsbuild/public/js/app.js"
                                        :output-dir "target/cljsbuild/public/js/out"
                                        :externs ["react/externs/react.js"]
                                        :pretty-print true}}}}
  
  :target-path "target/%s/"

  :profiles {:uberjar {:omit-source true
                       :prep-tasks ["compile" ["cljsbuild" "once"]]
                       :cljsbuild {:builds {:app {:source-paths ["env/prod/cljs"]
                                                  :compiler {:optimizations :advanced
                                                             :pretty-print false
                                                             :closure-warnings {:externs-validation :off
                                                                                :non-standard-jsdoc :off}}}}} 
                       :aot :all
                       :uberjar-name "dwace.jar"
                       :source-paths ["env/prod/clj"]
                       :resource-paths ["env/prod/resources"]}
             
             :dev           [:project/dev :profiles/dev]
             
             :test          [:project/test :profiles/test]
             
             :project/dev  {:dependencies [[prone "1.1.4"]
                                           [ring/ring-mock "0.3.0"]
                                           [ring/ring-devel "1.5.0"]
                                           [pjstadig/humane-test-output "0.8.1"]
                                           [lein-figwheel "0.5.8"]
                                           [figwheel-sidecar "0.5.8"]
                                           [lein-doo "0.1.7"]
                                           [com.cemerick/piggieback "0.2.1"]]
                            
                            :plugins      [[com.jakemccrary/lein-test-refresh "0.14.0"]
                                           [lein-figwheel "0.5.8"]
                                           [lein-doo "0.1.7"]
                                           [org.clojure/clojurescript "1.9.293"]]
                  
                            :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]
                                                       :compiler {:main "dwace.app"
                                                                  :asset-path "/js/out"
                                                                  :optimizations :none
                                                                  :source-map true}}
                                                 :test {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                                                        :compiler {:output-to "target/test.js"
                                                                   :main "dwace.doo-runner"
                                                                   :optimizations :whitespace
                                                                   :pretty-print true}}}} 
                            
                            :figwheel {:http-server-root "public"
                                       :nrepl-port 7002
                                       :css-dirs ["resources/public/css"]}
                            
                            :doo {:build "test"}
                            :source-paths ["env/dev/clj" "test/clj"]
                            :resource-paths ["env/dev/resources"]
                            :repl-options {:init-ns user
                                           :nrepl-middleware
                                           [cemerick.piggieback/wrap-cljs-repl]}
                            :injections [(require 'pjstadig.humane-test-output)
                                         (pjstadig.humane-test-output/activate!)]}
             
             :project/test {:resource-paths ["env/dev/resources" "env/test/resources"]}

             :profiles/dev {}
             :profiles/test {}})
