(defproject nbt-clj "0.1.1-SNAPSHOT"
  :description "A simple library to read Minecraft NBT data structures"
  :url "http://github.com/NathanWilliams/nbt-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  ;:jvm-opts ["-Xdebug" "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9900"]
  :profiles {:dev {:dependencies [[clj-ns-browser "1.3.2-SNAPSHOT"]
                                  [org.clojure/tools.namespace "0.2.2"]]}}

  :dependencies [[org.clojure/clojure "1.5.0"]
                 [gloss "0.2.2-beta5"]] ;DSL for reading binary data
  
  :main nbt-clj.devel)
