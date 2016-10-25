(ns everyday.string
  "The everyday.string API is roughly based on the Apache commons-lang3
   StringUtils API.

   It is meant as a drop-in replacement for `clojure.string`. We are
   consistent with their API but extend it a little."
  (:require
    [everyday.string.impl :as impl]))

(set! *warn-on-reflection* true)


;; -------------
;; empty checks

;; isEmpty / empty? -> clojure.core/empty?
;; isNotEmpty / not-empty? -> clojure.core/not-empty
;; isAnyEmpty / any-empty? -> (clojure.core/some clojure.core/empty? [coll])
;; isNoneEmpty / none-empty? -> (clojure.core/not-any? clojure.core/empty? [coll])

(defn blank?
  "True if s is nil, empty, or contains only whitespace, as defined
   by `Character/isWhitespace`.

   Examples:
   ```
   (blank? nil)         => true
   (blank? \"\")        => true
   (blank? \" \")       => true
   (blank? \"bob\")     => false
   (blank? \"  bob  \") => false
   ```"
  {:added "0.1"}
  [^CharSequence s]
  (if (or (nil? s)
          (= 0 (.length s)))
    true
    (loop [index (.length s)]
      (cond
        (= 0 index) true

        (Character/isWhitespace (.charAt s (unchecked-dec index)))
        (recur (unchecked-dec index))

        :else
        false))))

(def ^{:added "0.1"}
  not-blank?
  "False if is not nil, not empty and contains not only whitespace, as defined
   by `Character/isWhitespace`.

   Examples:
   ```
   (not-blank? nil)         => false
   (not-blank? \"\")        => false
   (not-blank? \" \")       => false
   (not-blank? \"bob\")     => true
   (not-blank? \"  bob  \") => true
   ```"
  (complement blank?))

;; isAnyBlank / any-blank? -> (clojure.core/some clojure.core/blank? [coll])
;; isNoneBlank / none-blank? -> (clojure.core/not-any? clojure.core/blank? [coll])


;; -------------
;; trim

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
  (impl/trim* s #(<= (int %) (int \space))))

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
    ""))

(defn triml
  "Removes control characters (char <= 32 or char <= \u0020) from the
   beginning of this String, handling nil by returning nil (nil-safe).

   To strip whitespace characters, use stripl.

   This function is fundamentaly different from clojure.string/triml as
   it also removes control characters smaller than the space character
   (\u0020).

   Examples:
   ```
   (triml nil)             => nil
   (triml \"\")            => \"\"
   (triml \"     \")       => \"\"
   (triml \"abc\")         => \"abc\"
   (triml \"    abc    \") => \"abc    \"
   ```"
  {:added "0.1"}
  [^CharSequence s]
  (impl/triml* s #(<= (int %) (int \space))))

;; TODO: triml-nil, triml-empty

(defn trimr
  "Removes control characters (char <= 32 or char <= \u0020) from the
   end of this String, handling nil by returning nil (nil-safe).

   To strip whitespace characters, use stripr.

   This function is fundamentaly different from clojure.string/triml as
   it also removes control characters smaller than the space character
   (\u0020).

   Examples:
   ```
   (triml nil)             => nil
   (triml \"\")            => \"\"
   (triml \"     \")       => \"\"
   (triml \"abc\")         => \"abc\"
   (triml \"    abc    \") => \"    abc\"
   ```"
  {:added "0.1"}
  [^CharSequence s]
  (impl/trimr* s #(<= (int %) (int \space))))

;; TODO: trimr-nil, trimr-empty


;; --------
;; truncate

(defn truncate
  "Truncates a string with an optional offset.

   Specifically:
   * If s is less than max-width characters long, return it.
   * Else truncate it, using substr from offset to max-width
   * If max-width must be at least 0.
   * In no case a String of length greater than max-width will
     be returned.
   * A nil String returns nil (Nil-safe)
   * offset can be omitted and defaults to 0

   Examples:
   ```
   ;; without offset:
   (truncate nil 0)          => nil
   (truncate nil 2)          => nil
   (truncate \"\" 4)         => \"\"
   (truncate \"abcdefg\" 4)  => \"abcd\"
   (truncate \"abcdefg\" 6)  => \"abcdef\"
   (truncate \"abcdefg\" 7)  => \"abcdefg\"
   (truncate \"abcdefg\" 8)  => \"abcdefg\"
   (truncate \"abcdefg\" -1) => (throws AssertionError)

   ;; with offset:
   (truncate nil 0 0)                                                  => nil
   (truncate nil 2 4)                                                  => nil
   (truncate \"\" 0 10)                                                => \"\"
   (truncate \"\" 2 10)                                                => \"\"
   (truncate \"abcdefghij\" 0 3)                                       => \"abc\"
   (truncate \"abcdefghij\" 5 6)                                       => \"fghij\"
   (truncate \"raspberry peach\" 10 15)                                => \"peach\"
   (truncate \"abcdefghijklmno\" 0 10)                                 => \"abcdefghij\"
   (truncate \"abcdefghijklmno\" -1 10)                                => (throws AssertionError)
   (truncate \"abcdefghijklmno\" Integer/MIN_VALUE 10)                 => \"abcdefghij\"
   (truncate \"abcdefghijklmno\" Integer/MIN_VALUE Integer/MAX_VALUE)  => \"abcdefghijklmno\"
   (truncate \"abcdefghijklmno\" 0 Integer/MAX_VALUE)                  => \"abcdefghijklmno\"
   (truncate \"abcdefghijklmno\" 1 10)                                 => \"bcdefghijk\"
   (truncate \"abcdefghijklmno\" 2 10)                                 => \"cdefghijkl\"
   (truncate \"abcdefghijklmno\" 3 10)                                 => \"defghijklm\"
   (truncate \"abcdefghijklmno\" 4 10)                                 => \"efghijklmn\"
   (truncate \"abcdefghijklmno\" 5 10)                                 => \"fghijklmno\"
   (truncate \"abcdefghijklmno\" 5 5)                                  => \"fghij\"
   (truncate \"abcdefghijklmno\" 5 3)                                  => \"fgh\"
   (truncate \"abcdefghijklmno\" 10 3)                                 => \"klm\"
   (truncate \"abcdefghijklmno\" 10 Integer/MAX_VALUE)                 => \"klmno\"
   (truncate \"abcdefghijklmno\" 13 1)                                 => \"n\"
   (truncate \"abcdefghijklmno\" 13 Integer/MAX_VALUE)                 => \"no\"
   (truncate \"abcdefghijklmno\" 14 1)                                 => \"o\"
   (truncate \"abcdefghijklmno\" 14 Integer/MAX_VALUE)                 => \"o\"
   (truncate \"abcdefghijklmno\" 15 1)                                 => \"\"
   (truncate \"abcdefghijklmno\" 15 Integer/MAX_VALUE)                 => \"\"
   (truncate \"abcdefghijklmno\" Integer/MAX_VALUE/ Integer.MAX_VALUE) => \"\"
   (truncate \"abcdefghij\" 3 -1)                                      => (throws AssertionError)
   (truncate \"abcdefghij\" -2 4)                                      => (throws AssertionError)
   ```"
  ([^CharSequence s max-width]
   (truncate s 0 max-width))
  ([^CharSequence s offset max-width]
   (assert (not (neg? offset)) "offset cannot be negative")
   (assert (not (neg? max-width)) "max-width cannot be negative")
   (cond
     (nil? s)
     nil

     (>= offset (.length s))
     ""

     (> (.length s) max-width)
     (subs s offset (if (> (+ offset max-width) (.length s)) (.length s) (+ offset max-width)))

     :else
     (subs s offset))))


;; --------
;; strip

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
  (impl/trim* s #(Character/isWhitespace ^char %)))

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
    ""))

(defn stripl
  "Removes whitespace characters from the beginning of this String, handling
   nil by returning nil (nil-safe).

   This function is similar to `triml` but removes whitespace, as defined by
   `Character/isWhitespace`.

   Examples:
   ```
   (stripl nil)             => nil
   (stripl \"\")            => \"\"
   (stripl \"     \")       => \"\"
   (stripl \"abc\")         => \"abc\"
   (stripl \"  abc\")       => \"abc\"
   (stripl \"abc  \")       => \"abc  \"
   (stripl \" abc \")       => \"abc \"
   (stripl \"    ab c   \") => \"ab c   \"
  ```"
  {:added "0.1"}
  [^CharSequence s]
  (impl/triml* s #(Character/isWhitespace ^char %)))

;; TODO: stripl-nil, stripl-empty

(defn stripr
  "Removes whitespace characters from the end of this String, handling
   nil by returning nil (nil-safe).

   This function is similar to `trimr` but removes whitespace, as defined by
   `Character/isWhitespace`.

   Examples:
   ```
   (stripr nil)             => nil
   (stripr \"\")            => \"\"
   (stripr \"     \")       => \"\"
   (stripr \"abc\")         => \"abc\"
   (stripr \"  abc\")       => \"  abc\"
   (stripr \"abc  \")       => \"abc\"
   (stripr \" abc \")       => \" abc\"
   (stripr \"    ab c   \") => \"    ab c\"
  ```"
  {:added "0.1"}
  [^CharSequence s]
  (impl/trimr* s #(Character/isWhitespace ^char %)))

;; TODO: stripr-nil, stripr-empty

;;

(defn ^String trim-newline
  "Removes all trailing newline \\n or return \\r characters from
   string.  Similar to Perl's chomp."
  {:added "0.1"}
  [^CharSequence s]
  (impl/trimr* s #(or (= % \newline) (= % \return))))
