(def artifact
  {:project
   'everyday/everyday

   :version
   "0.1.0-SNAPSHOT"

   :description
   "A clojure library for everyday use."

   :url
   "https://github.com/rkoch/everyday"

   :scm
   {:name "git"
    :url "https://github.com/rkoch/everyday.git"}

   :license
   {"Eclipse Public License 1.0 (EPL-1.0)" "http://www.eclipse.org/legal/epl-v10.html"}})


(task-options!
  pom artifact

  jar {:file (str "everyday-" (:version artifact) ".jar")})


(set-env!
  :source-paths
  #{"src/main/clojure"}

  :resource-paths
  #{"src/main/resources"}

  :dependencies
  '[[org.clojure/clojure    "1.7.0"
     :scope "provided"]

    ;; dev dependencies
    [adzerk/boot-test       "1.1.2"
     :scope "test"]
    [adzerk/bootlaces       "0.1.13"
     :scope "test"]])

(require '[adzerk.boot-test :refer :all])
(require '[adzerk.bootlaces :refer :all])

;; TODO: Fix scm tag (use git-describe)
;; TODO: Use tag when releasing
;; TODO: Build version file
;; TODO: Use sift to remove .gitkeep, etc.
(bootlaces!
  (:version artifact)
  :dont-modify-paths? true)


(deftask dev
  "Pull in test dependencies."
  []
  (merge-env!
    :source-paths
    #{"src/test/clojure"}

    :resource-paths
    #{"src/test/resources"})
  identity)


(deftask remove-ignored
  []
  (sift
    :invert true
    :include #{#".*\.swp" #".gitkeep"}))


(replace-task!
  [t test]
  (fn [& xs]
    (comp (dev)
          (apply t xs)))
  [b build-jar]
  (fn [& xs]
    (merge-env!
      :resource-paths #{"src/main/clojure"})
    (comp (remove-ignored)
          (apply b xs))))


;; vim: ft=clojure:
