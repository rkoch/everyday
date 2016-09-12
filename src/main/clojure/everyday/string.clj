(ns everyday.string
  "The everyday.string API is roughly based on the Apache commons-lang3
   StringUtils API.

   It is meant as a drop-in replacement for `clojure.string`. We are
   consistent with their API but extend it a little"
  (:require
    [clojure.string :as str]))


(def ^:const EMPTY
  "The empty String."
  "")

(def ^:const SPACE-CHAR
  \ )

(def ^:const SPACE-VALUE
  (int SPACE-CHAR))

(def ^:const SPACE
  "The space String."
  (str SPACE-CHAR))


(defn- trim*
  "Private function to remove characters at the beginning or end of a string."
  [^CharSequence s strip-char?]
  (when s
    (let [len (.length s)]
      ;; find boundary from the end of the string
      (loop [rindex len]
        (if (zero? rindex)
          EMPTY
          (if (strip-char? (.charAt s (dec rindex)))
            (recur (dec rindex))
            ;; find boundary from the beginning of the string;
            ;; no need to check for length here as there is at
            ;; least one usable char in the string.
            (loop [lindex 0]
              (if (strip-char? (.charAt s lindex))
                (recur (inc lindex))
                ;; only create subSequence if there is anything to trim
                (if (or (pos? lindex) (< rindex len))
                  (.. s (subSequence lindex rindex) toString)
                  s)))))))))

(defn trim
  "Removes control characters (char <= 32 or char <= \u0020) from both
   ends of this String, handling nil by returning nil (nil-safe).

   To strip whitespace characters, use strip.

   This function is fundamentaly different from clojure.string/trim as
   it also removes control characters smaller than the space character
   (\u0020).

   Examples:
   ```
   (trim nil)             => nil
   (trim \"\")            => \"\"
   (trim \"     \")       => \"\"
   (trim \"abc\")         => \"abc\"
   (trim \"    abc    \") => \"abc\"
   ```"
  {:added "0.1"}
  [^CharSequence s]
  (trim* s #(<= (int %) SPACE-VALUE)))

(defn trim-nil
  "Removes control characters (char <= 32 or char <= \u0020) from both
   ends of this String, returning nil if the String is empty (\"\") after
   the trim or if it is nil (nil-safe).

   To strip whitespace characters, use strip-nil.

   This function is fundamentaly different from clojure.string/trim as
   it also removes control characters smaller than the space character
   (\u0020).

   Examples:
   ```
   (trim-nil nil)             => nil
   (trim-nil \"\")            => nil
   (trim-nil \"     \")       => nil
   (trim-nil \"abc\")         => \"abc\"
   (trim-nil \"    abc    \") => \"abc\"
   ```"
  {:added "0.1"}
  [^CharSequence s]
  (when-some [s' (trim s)]
    (when (seq s')
      s')))

(defn trim-empty
  "Removes control characters (char <= 32 or char <= \u0020) from both
   ends of this String, returning an empty string (\"\") if the String
   is empty (\"\") after the trim or if it is nil (nil-safe).

   To strip whitespace characters, use strip-empty.

   This function is fundamentaly different from clojure.string/trim as
   it also removes control characters smaller than the space character
   (\u0020).

   Examples:
   ```
   (trim-empty nil)             => \"\"
   (trim-empty \"\")            => \"\"
   (trim-empty \"     \")       => \"\"
   (trim-empty \"abc\")         => \"abc\"
   (trim-empty \"    abc    \") => \"abc\"
   ```"
  {:added "0.1"}
  [^CharSequence s]
  (if s
    (trim s)
    EMPTY))


(defn strip
  "Removes whitespace characters from both ends of this String, handling
   nil by returning nil (nil-safe).

   This function is similar to `trim` but removes whitespace, as defined by
   `Character/isWhitespace`.

   Examples:
   ```
   (strip nil)             => nil
   (strip \"\")            => \"\"
   (strip \"     \")       => \"\"
   (strip \"abc\")         => \"abc\"
   (strip \"  abc\")       => \"abc\"
   (strip \"abc  \")       => \"abc\"
   (strip \" abc \")       => \"abc\"
   (strip \"    ab c   \") => \"ab c\"
   ```"
  {:added "0.1"}
  [^CharSequence s]
  (trim* s #(Character/isWhitespace %)))


(defn strip-nil
  "Removes whitespace characters from both ends of this String, returning
   nil if the String is empty (\"\") after the strip or if it is nil
   (nil-safe).

   This function is similar to `trim-nil` but removes whitespace, as defined
   by `Character/isWhitespace`.

   Examples:
   ```
   (strip-nil nil)             => nil
   (strip-nil \"\")            => nil
   (strip-nil \"     \")       => nil
   (strip-nil \"abc\")         => \"abc\"
   (strip-nil \"  abc\")       => \"abc\"
   (strip-nil \"abc  \")       => \"abc\"
   (strip-nil \" abc \")       => \"abc\"
   (strip-nil \"    ab c   \") => \"ab c\"
   ```"
  {:added "0.1"}
  [^CharSequence s]
  (when-some [s' (strip s)]
    (when (seq s')
      s')))

(defn strip-empty
  "Removes whitespace characters from both ends of this String, returning
   an empty string (\"\") if the String is empty (\"\") after the strip or
   if it is nil (nil-safe).

   This function is similar to `trim-empty` but removes whitespace, as defined
   by `Character/isWhitespace`.

   Examples:
   ```
   (strip-nil nil)             => \"\"
   (strip-nil \"\")            => \"\"
   (strip-nil \"     \")       => \"\"
   (strip-nil \"abc\")         => \"abc\"
   (strip-nil \"  abc\")       => \"abc\"
   (strip-nil \"abc  \")       => \"abc\"
   (strip-nil \" abc \")       => \"abc\"
   (strip-nil \"    ab c   \") => \"ab c\"
   ```"
  {:added "0.1"}
  [^CharSequence s]
  (if s
    (strip s)
    EMPTY))
