(ns acronymium.core-test
  (:require
    [acronymium.core :refer :all]
    [expectations :refer :all]
    [clojure.java.io :as io])
  (:import (acronymium.core Rule Letter)))

(def base-rules (-> (new-rules)
                    (must-have-one-of ["strategy" "Plan"])
                    (must-have-one-of ["goals" "ambition" "objectives"])
                    (must-have-one-of ["Purpose" "Vision"])
                    (must-have-one-of ["Customer" "Value" "Proposition"])
                    (must-have-one-of ["Delivery" "Execution" "Operations" "Process"])
                    (can-have-one-of ["Perspective"])
                    (can-have-one-of ["Technology" "Digital"])))


; Some assumptions
(expect [\S \t \r \i \n \g] (seq "String"))

; About rules

(expect {} (new-rules))

(let [rules (-> (new-rules)
                (must-have-one-of ["Strategy" "Plan"]))]
  (expect {\A (Rule. :required [(Letter. \S "Strategy") (Letter. \P "Plan")])} rules))

(let [rules (-> (new-rules)
                (can-have-one-of ["Technology" "Digital"]))]
  (expect {\A (Rule. :optional [(Letter. \T "Technology") (Letter. \D "Digital")])} rules))

(let [rule (Rule. :required [(Letter. \S "Strategy") (Letter. \P "Plan")])]
  (expect (rule-has-letter? rule \S))
  (expect (rule-has-letter? rule \P))
  (expect (not (rule-has-letter? rule \A))                  ; and all the others we assume
          ))


(let [rule (Rule. :required [(Letter. \S "Strategy") (Letter. \P "Plan")])]
  (expect "Strategy" (letter->rule-word rule \S)))

; Convert letters to rules, where there is no rule that can match a letter we expect
; an empty mapping [\chr []]

(expect [\A] (letter->matching-rules base-rules \S))
(expect [\A \C \D \E \F] (letter->matching-rules base-rules \P))

(expect [[\S [\A]] [\T [\G]] [\R []] [\I []] [\N []] [\G [\B]]] (letters->rules base-rules (seq "STRING")))
(expect [[\S [\A]] [\P [\A \C \D \E \F]] [\A [\B]] [\C [\D]] [\E [\E]]] (word->labellings base-rules "space"))

(expect (has-no-solution? (word->labellings base-rules "string")))
(expect (not (has-no-solution? (word->labellings base-rules "space"))))

; Find all possible solutions for labelling the word

(let [solutions (solutions base-rules "space")]
  (expect 2 (count solutions))
  (expect [[\S \A] [\P \C] [\A \B] [\C \D] [\E \E]] (first solutions))
  (expect [[\S \A] [\P \F] [\A \B] [\C \D] [\E \E]] (second solutions)))

(let [solutions (solutions base-rules "string")]
  (expect 0 (count solutions)))

(expect ["Strategy" "Purpose" "Ambition" "Customer" "Execution"]
        (acronym base-rules [[\S \A] [\P \C] [\A \B] [\C \D] [\E \E]]))

(expect [["Strategy" "Purpose" "Ambition" "Customer" "Execution"]
         ["Strategy" "Perspective" "Ambition" "Customer" "Execution"]] (acroynms base-rules "space"))

(let [dict (load-dict (io/resource "wordlist_60k.csv"))]
  (expect 6900 (count dict)))

