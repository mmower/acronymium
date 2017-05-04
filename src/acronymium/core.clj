(ns acronymium.core
  (:require [clojure.set :as set]
            [clojure.string :as s]
            [clojure.math.combinatorics :as c]
            [clojure.string :as str]))

(def alphabet #{\A \B \C \D \E
                \F \G \H \I \J
                \K \L \M \N \O
                \P \Q \R \S \T
                \U \V \W \X \Y \Z})


;; The word-classes represent a series of word categories containing words that can be used to
;; describe the category, ordered from most to least desirable. A word category has the form
;;
;; [category word-list must-or-optional]
;;
;; The category is purely description
;; The word-list is a series of words, ordered from most to least desirable, used to represent the category
;; The must-or-optional determines whether a word from the category must be represented

(def word-classes [[:strategy ["Strategy" "Plan"] :must]
                   [:purpose ["Purpose" "Vision"] :must]
                   [:perspective ["Perspective"] :optional]
                   [:goals ["Goals" "Ambition" "Objective"] :must]
                   [:service ["Passion" "Service"] :optional]
                   [:pain ["Customers" "Audience" "Needs"] :must]
                   [:return ["Return" "Profit"] :optional]
                   [:value ["Value" "Proposition"] :must]
                   [:tech ["Technology" "Digital"] :optional]
                   [:innovation ["Innovation" "Design"] :optional]
                   [:execution ["Delivery" "Execution" "Operations" "Process"] :must]])

(defn equivalent-words [word-classes word]
  (let [pred (fn [[_ words _]] (some #(= word %) words))]
    (->> word-classes
         (filter pred)
         (first)
         (second))))

(defn word-tree [word-classes]
  (reduce (fn [tree [_ words must-or-optional]]
            (reduce (fn [tree word]
                      (let [letter (first word)]
                        (update-in tree [must-or-optional letter] (fnil conj []) word))
                      ) tree (map str/capitalize words))) {} word-classes))


(defn legal-letters [word-classes]
  (->> word-classes
       (map second)
       (flatten)
       (map s/upper-case)
       (map first)
       (distinct)
       (set)))

(defn illegal-letters [word-classes]
  (set/difference alphabet (legal-letters word-classes)))

(defn illegal-letter-filter-fn [word-classes]
  (let [illegals (illegal-letters word-classes)]
    (fn [word]
      (not-empty (set/intersection (set word) illegals)))))

(defn create-letter-sets [word-classes must-value]
  (let [must-pred    (fn [[_ _ must-or-optional]]
                       (= must-value must-or-optional))
        must-reducer (fn [coll [_ words _]]
                       (conj coll (set (map (comp first s/upper-case) words))))]
    (->> word-classes
         (filter must-pred)
         (reduce must-reducer #{}))))

(def must-sets (create-letter-sets word-classes :must))
(def optional-sets (create-letter-sets word-classes :optional))

(def all-must-sets (apply c/cartesian-product must-sets))

(def min-word-len (count must-sets))
(def max-word-len (+ min-word-len (count optional-sets)))

(println (str "Words can vary between " min-word-len " and " max-word-len " characters."))

(defn same-letter-frequency? [letter-frequencies template-frequencies]
  (every? (fn [[letter freq]]
            (= freq (get letter-frequencies letter))) template-frequencies))

(defn has-required-letters-fn [word-classes]
  (let [required-letters-freq-maps (->> (create-letter-sets word-classes :must)
                                        (apply c/cartesian-product)
                                        (map frequencies))]
    (fn [word]
      (let [letter-frequencies (frequencies word)]
        (some (fn [template-frequencies]
                (same-letter-frequency? letter-frequencies template-frequencies)) required-letters-freq-maps)))))

(defn load-dict [dict-file]
  (let [required-letters-pred (has-required-letters-fn word-classes)
        illegal-letters-pred  (illegal-letter-filter-fn word-classes)]
    (->> dict-file
         (slurp)                                            ; read the dictionary
         (s/split-lines)                                    ; a word per-line
         (map s/upper-case)
         (filter #(>= (count %) min-word-len))              ; only words longer than
         (filter #(<= (count %) max-word-len))              ; only words shorter than
         (remove illegal-letters-pred)                      ; none with illegal letters
         (filter required-letters-pred))))                  ; pass with correct legal required legal letter

(def dict (load-dict "/usr/share/dict/words"))

(defn group-by-length [dict]
  (reduce (fn [coll word]
            (update coll (count word) conj word)) {} dict))

(def dict (-> (load-dict "/usr/share/dict/words")
              (group-by-length)))

(def root-tree (word-tree word-classes))

(defn print-args [& args]
  (doseq [[n arg] (partition 2 (interleave (range) args))]
    (println (str n "> " arg))))

(defn word-is-pred [target-word]
  (fn [w]
    (= w target-word)))

(defn remove-2 [s p]
  (remove p s))

(defn tree-without-class [tree must-or-optional letter class-word]
  (let [words-to-remove (equivalent-words word-classes class-word)]
    (reduce (fn [tree word]
              (let [pred   (word-is-pred word)
                    letter (first word)]
                (update-in tree [must-or-optional letter] remove-2 pred))) tree words-to-remove)))


(defn word-of-length [dict length]
  (let [word (rand-nth (get dict length))]
    (loop [letters word
           acronym []
           tree    root-tree]
      (if (empty? letters)
        [word acronym]
        (let [letter (first letters)]
          (if-let [choice (first (get-in tree [:must letter]))]
            (recur (rest letters) (conj acronym choice) (tree-without-class tree :must letter choice))
            (if-let [choice (first (get-in tree [:optional letter]))]
              (recur (rest letters) (conj acronym choice) (tree-without-class tree :optional letter choice))
              (recur [] [] nil))))))))

(defn no-acronym [[word acronym]]
  (empty? acronym))

(defn words-of-length [dict length]
  (distinct (remove no-acronym (lazy-seq (cons (word-of-length dict length) (words-of-length dict length))))))


