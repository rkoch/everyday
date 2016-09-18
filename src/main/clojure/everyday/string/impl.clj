(ns ^:no-doc everyday.string.impl)

(defn trim*
  "Private function to remove characters at the beginning or end of a string."
  [^CharSequence s strip-char?]
  (when s
    (let [len (.length s)]
      ;; find boundary from the end of the string
      (loop [rindex len]
        (if (zero? rindex)
          ""
          (if (strip-char? (.charAt s (unchecked-dec rindex)))
            (recur (unchecked-dec rindex))
            ;; find boundary from the beginning of the string;
            ;; no need to check for length here as there is at
            ;; least one usable char in the string.
            (loop [lindex 0]
              (if (strip-char? (.charAt s lindex))
                (recur (unchecked-inc lindex))
                ;; only create subSequence if there is anything to trim
                (if (or (pos? lindex) (< rindex len))
                  (.. s (subSequence lindex rindex) toString)
                  s)))))))))

(defn triml*
  "Private function to remove characters at the beginning of a string."
  [^CharSequence s strip-char?]
  (when s
    (let [len (.length s)]
      (loop [index 0]
        (if (= len index)
          ""
          (if (strip-char? (.charAt s index))
            (recur (unchecked-inc index))
            (.. s (subSequence index len) toString)))))))

(defn trimr*
  "Private function to remove characters at end of a string."
  [^CharSequence s strip-char?]
  (when s
    (loop [index (.length s)]
      (if (zero? index)
        ""
        (if (strip-char? (.charAt s (unchecked-dec index)))
          (recur (unchecked-dec index))
          (.. s (subSequence 0 index) toString))))))
