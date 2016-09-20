(ns everyday.set
  "The everyday.set is meant as an extension (and not replacment) to `clojure.set`.
   However, some functions with the same name might behave differently. But that will
   be noted."
  (:require
    [clojure.set :as cset]))

(set! *warn-on-reflection* true)


(defn rename-keys
  "Returns the map with the keys in kmap renamed to the vals in kmap.
   If kmap is a function the function is applied to every key in the map."
  {:added "0.1"}
  [map kmap]
  (if (fn? kmap)
    (persistent!
      (reduce
        (fn [m [k v]] (assoc! m (kmap k) v))
        (transient (empty map))
        map))
    (cset/rename-keys map kmap)))
