(ns leiningen.bikeshed
  (:require [leiningen.core.eval :as lein]))

(defn help
  "Help text displayed from the command line"
  []
  "Bikesheds your project with totally arbitrary criteria.")

(defn bikeshed
  "Main function called from Leiningen"
  [project & args]
  (lein/eval-in-project
   (-> project
       (update-in [:dependencies] conj ['lein-bikeshed-ona "0.2.1-SNAPSHOT"]))
   `(let [[opts# args# banner#]
          (clojure.tools.cli/cli
           '~args
           ["-H" "--help-me" "Show help"
            :flag true :default false]
           ["-v" "--verbose" "Display missing doc strings"
            :flag true :default false]
           ["-m" "--max-line-length" "Max line length"
            :default nil
            :parse-fn #(Integer/parseInt %)]
           ["-d" "--doc-strings" "Check for missing doc strings"
            :flag true :default false]
           ["-c" "--check-colliding" "Check for colliding arguments"
            :flag true :default false]
           ["-r" "--check-redefs" "Check for with redefs"
            :flag true :default false])]
      '~project
      (when (:help-me opts#)
        (println banner#)
        (System/exit 0))
      (if (bikeshed.core/bikeshed
           '~project (select-keys opts# [:max-line-length
                                         :verbose
                                         :doc-strings
                                         :check-colliding
                                         :check-redefs]))
        (System/exit -1)
        (System/exit 0)))
   '(do
      (require 'bikeshed.core)
      (require 'clojure.tools.cli))))
