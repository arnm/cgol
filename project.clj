(defproject cgol "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [sablono "0.2.21"]
                 [om "0.7.1"]
                 [prismatic/om-tools "0.3.2"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src"]
                        :compiler {
                                   :output-to "public/js/cgol.js"
                                   :output-dir "public/js/dev"
                                   :optimizations :none
                                   :source-map true}}
                       {:id "prod"
                        :source-paths ["src"]
                        :compiler {
                                   :output-to "public/js/cgol.js"
                                   :pretty-print false
                                   :externs ["public/bower_components/react/react.min.js"]
                                   :optimizations :advanced}}]})
