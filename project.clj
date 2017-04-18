(defproject formious/formious "0.4.0-SNAPSHOT"
  :description "Experiment Server"
  ; "keywords": ["forms", "surveys"],
  ; "homepage": "https://github.com/chbrown/formious",
  ; "repository": "git://github.com/chbrown/formious.git",
  ; "author": "Christopher Brown <io@henrian.com> (http://henrian.com)",
  ; "license": "MIT",
  :url "https://formious.com/"
  :dependencies [; clj
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.postgresql/postgresql "42.0.0"]
                 [org.clojure/data.json "0.2.6"]
                 [http.async.client "1.2.0"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [net.sf.supercsv/super-csv "2.4.0"]
                 [net.sf.supercsv/super-csv-java8 "2.4.0"]
                 [org.apache.poi/poi-ooxml "3.16"]
                 ; [amazonica "0.3.93"]
                 [com.amazonaws/aws-java-sdk-mechanicalturkrequester "1.11.119" :exclusions [joda-time]]
                 [org.clojure/tools.logging "0.3.1"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 ; clj/web
                 [ring/ring-core "1.6.0-RC2"]
                 [ring/ring-devel "1.6.0-RC2"]
                 [ring-data.json "0.1.0"]
                 [liberator "0.14.1"]
                 [http-kit "2.2.0"]
                 ; cljs
                 [bidi "2.0.16"]
                 ; pin cljs at 1.9.456 until goo.gl/hfcbWu is closed; see also git.io/vS6Fp
                 [org.clojure/clojurescript "1.9.456"]
                 [org.clojure/core.async "0.3.442"]
                 ; avoid "cljs.core/uuid? being replaced by: cognitect.transit/uuid?" warning:
                 [com.cognitect/transit-cljs "0.8.239"]
                 [rum "0.10.8"]
                 [cljs-http "0.1.42"]]
  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-ring "0.11.0"]
            [lein-figwheel "0.5.10" :exclusions [org.clojure/clojure]]]
  :clean-targets ^{:protect false} [:target-path
                                    "resources/public/build/bundle.js"
                                    "resources/public/build/out"]
  ; :production must be the first cljsbuild specified, since there is
  ; no way to select a build by name in the `lein (uber)jar` command
  :cljsbuild {:builds
              [{:id "production"
                :source-paths ["src"]
                ; :jar true ; not needed since output goes into cljs anyway
                :compiler {:output-to "resources/public/build/bundle.js"
                           :optimizations :simple
                           :pretty-print false}}
               {:id "dev"
                :figwheel {:on-jsload formious.client.core/figwheel-on-jsload!}
                :source-paths ["src"]
                :compiler {:main formious.client.core ; used by figwheel
                           ; figwheel requires these build/out directives:
                           :asset-path "/build/out" ; maybe used by figwheel?
                           :output-to "resources/public/build/bundle.js"
                           :output-dir "resources/public/build/out" ; for temporary/intermediate files only
                           :preloads [devtools.preload]
                           :optimizations :none}}]}
  :figwheel {:ring-handler formious.server.core/reloadable-handler
             :server-port 1451
             :server-ip "127.0.0.1"
             :css-dirs ["resources/public/build"]
             :server-logfile "/tmp/formious-figwheel.log"}
  :ring {:handler formious.server.core/reloadable-handler
         :port 1451
         :open-browser? false}
  :main formious.server.core
  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.3"]
                                  [figwheel-sidecar "0.5.10"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.namespace "0.3.0-alpha3"]]
                   :ring {:auto-refresh? true}
                   :source-paths ["src" "dev"]
                   :repl-options {:init-ns user
                                  ; piggieback enables cljs in nREPL
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}
             :uberjar {:aot :all
                       :hooks [leiningen.cljsbuild]}
             :production {:hooks [leiningen.cljsbuild]
                          :ring {:stacktraces? false
                                 :auto-reload? false}}})
