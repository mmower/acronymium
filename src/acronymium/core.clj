(ns acronymium.core
  (:require [clojure.set :as set]
            [clojure.string :as s]
            [clojure.math.combinatorics :as c]
            [clojure.string :as str]))

(comment
  (def merge-with-+ (partial merge-with +))
  (defn add-rule [rule-set required words]
    (let [words   (map str/capitalize words)
          letters (map first words)
          fname   (keyword (str (name required) "-frequencies"))]
      (as-> rule-set $
            (update $ :rules assoc (char (+ (count (:rules $)) 65)) words)
            (update $ required conj words)
            (update $ fname merge-with-+ (frequencies letters))
            (update $ :legal (fn [legal]
                               (reduce conj legal letters)))))))

(defrecord Rule [required letters])

(defrecord Letter [alpha word])

(defn make-letter [word]
  (let [word (str/capitalize word)]
    (Letter. (first word) word)))

(defn new-rules [] {})

(defn add-rule [rules required words]
  (let [words      (map str/capitalize words)
        letters    (map make-letter words)
        rule-count (count rules)
        label      (char (+ 65 rule-count))                 ; 65 ASCII 'A'
        rule       (Rule. required letters)
        ]
    (assoc rules label rule)))

(defn must-have-one-of [rule-set words]
  (add-rule rule-set :required words))

(defn can-have-one-of [rule-set words]
  (add-rule rule-set :optional words))

(defn required-rules [rule-set]
  (->> rule-set
       (filter (fn [[label rule]]
                 (= :required (:required rule))))
       (map first)))

(defn rule-has-letter? [rule letter]
  (some #(= letter (:alpha %)) (:letters rule)))

(defn letter->matching-rules [rules letter]
  "Given rules and an uppercase letter return a vector containing the label for
  each rule that could match that letter.
  (letter->matching-rules S) => [A]"
  (->> rules
       (filter (fn [[label rule]] (rule-has-letter? rule letter)))
       (mapv first)))

(defn letters->rules [rules letters]
  "Given rules and a seq of uppercase letters return a vector of mappings where
  each mapping is a vector composed of a letter and a vector of all labels of
  rules that can match the letter.
  (letters->rules rules [S P A C E]) => [[S [...]] [P [...]] ...]"
  (mapv (fn [letter]
          [letter (letter->matching-rules rules letter)]) letters))

(defn word->labellings [rules word]
  "Given rules and a word return a vector of mappings where each mapping is
  composed of a vector composed of a letter and a vector of all labels of rules
  that can match that letter.
  (word->labellings rules 'space') => [[S [...]] [P [...]] ...]"
  (let [letters    (-> word
                       (str/upper-case)
                       (seq))
        labellings (letters->rules rules letters)]
    labellings))

(defn has-no-solution? [labelling]
  "Returns true if an attempt at labelling returns a letter for which no rules
  are able to match the letter."
  (some empty? (map second labelling)))

(defn permute-solutions [sol]
  (loop [results []
         current (first sol)
         tail    (rest sol)]
    (if (nil? current)
      results
      (if (empty? results)
        (recur [current] (first tail) (rest tail))
        (recur (for [r results
                     s current]
                 (conj r s)) (first tail) (rest tail))))))

(defn label-used-more-than-once? [labels]
  (some #(> % 1) (vals (frequencies labels))))

(defn required-label-missing? [rules labels]
  (let [rs (set (required-rules rules))
        ls (set labels)]
    (not (set/subset? rs ls))))

(defn invalid-labelling? [rules labelling]
  "Given a labelling such as [[S A] [P D] [A B] [C D] [E E]] return false
  as it uses rule D twice twice (for letter P and C)"
  (let [labels (map second labelling)]
    (or (label-used-more-than-once? labels)
        (required-label-missing? rules labels))))

(defn solutions [rules word]
  "Return all valid solutions for combining rules to form the given word."
  (let [labellings (word->labellings rules word)
        ]
    (if (has-no-solution? labellings)
      []
      (->> labellings
           (mapv (fn [[letter rules]]                       ; e.g. letter:\S rules:[\A \B \D]
                   (mapv (fn [a b]
                           [a b]) (repeat letter) rules)))
           (permute-solutions)
           (remove (partial invalid-labelling? rules))))))

(defn letter->rule-word [rule letter]
  (->> (:letters rule)
       (filter #(= letter (:alpha %)))
       (first)
       (:word)))

(defn acronym [rules solution]
  (mapv (fn [[letter label]]
          (let [rule (get rules label)]
            (letter->rule-word rule letter))) solution))

(defn acroynms [rules word]
  "Given a word return all legal acronyms that can be formed for that word."
  (map (partial acronym rules) (solutions rules word)))

(def legal-char-pred (partial re-matches #"\w+"))

(defn load-dict [dict-file]
  (let [#_required-letters-pred #_(has-required-letters-fn word-classes)
        #_illegal-letters-pred  #_(illegal-letter-filter-fn word-classes)]
    (->> dict-file
         (slurp)                                          ; read the dictionary
         (s/split-lines)                                  ; a word per-line
         (map s/upper-case)
         (filter legal-char-pred)
         #_(filter #(>= (count %) min-word-len))            ; only words longer than
         #_(filter #(<= (count %) max-word-len))            ; only words shorter than
         #_(remove illegal-letters-pred)                    ; none with illegal letters
         #_(filter required-letters-pred))))

