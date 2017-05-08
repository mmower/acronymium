(ns acronymium.core
  (:require [clojure.set :as set]
            [clojure.string :as s]
            [clojure.math.combinatorics :as c]
            [clojure.string :as str]
            ))

(defrecord Rule [required letters])

(defn letter-map [words]
  (reduce (fn [coll w]
            (assoc coll (first w) w)) {} words))

(defn new-ruleset [] {:rules           {}
                      :required-labels []})

(defn add-rule [ruleset required words]
  (let [words      (map str/capitalize words)
        letters    (letter-map words)
        rule-count (count (get ruleset :rules))
        label      (char (+ 65 rule-count))                 ; 65 ASCII 'A'
        rule       (Rule. required letters)]
    (if (= :required required)
      (-> ruleset
          (assoc-in [:rules label] rule)
          (update :required-labels conj label))
      (assoc-in ruleset [:rules label] rule))))

(defn must-have-one-of [ruleset words]
  (add-rule ruleset :required words))

(defn can-have-one-of [ruleset words]
  (add-rule ruleset :optional words))

(defn required-rules [ruleset]
  (:required-labels ruleset))

(defn rule-has-letter? [rule letter]
  (some #(= letter %) (keys (get rule :letters))))

(defn letter->matching-rules [ruleset letter]
  "Given rules and an uppercase letter return a vector containing the label for
  each rule that could match that letter.
  (letter->matching-rules S) => [A]"
  (->> (:rules ruleset)
       (filter (fn [[label rule]] (rule-has-letter? rule letter)))
       (mapv first)))

(defn letters->rules [ruleset letters]
  "Given rules and a seq of uppercase letters return a vector of mappings where
  each mapping is a vector composed of a letter and a vector of all labels of
  rules that can match the letter.
  (letters->rules rules [S P A C E]) => [[S [...]] [P [...]] ...]"
  (mapv (fn [letter]
          [letter (letter->matching-rules ruleset letter)]) letters))

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

(defn permute-labellings [[letter labels]]
  (partition 2 (interleave (repeat letter) labels)))

(defn permute-solutions [labellings]
  (apply c/cartesian-product (map permute-labellings labellings)))

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

(defn candidate-solutions [rules word]
  (let [labellings (word->labellings rules word)]
    (if (has-no-solution? labellings)
      []
      (->> labellings
           (permute-solutions)))))

(defn solutions [rules word]
  "Return all valid solutions for combining rules to form the given word."
  (->> (candidate-solutions rules word)
       (remove (partial invalid-labelling? rules))))

(defn letter->rule-word [rule letter]
  (get-in rule [:letters letter]))

(defn acronym [ruleset solution]
  (mapv (fn [[letter label]]
          (let [rule (get-in ruleset [:rules label])]
            (letter->rule-word rule letter))) solution))

(defn acronyms [rules word]
  "Given a word return all legal acronyms that can be formed for that word."
  (map (partial acronym rules) (solutions rules word)))

(def legal-char-pred (partial re-matches #"\w+"))

(defn load-dict [dict-file]
  (let []
    (->> dict-file
         (slurp)                                            ; read the dictionary
         (s/split-lines)                                    ; a word per-line
         (map s/upper-case)
         (filter legal-char-pred))))

(defn acro-dict [rules word-list]
  (->> word-list
       (map (fn [word]
              [word (acronyms rules word)]))
       (filter (fn [[word list]]
                 (> (count list) 0)))
       (reduce (fn [coll [word acros]]
                 (assoc coll word acros)) {})))