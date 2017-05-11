(ns acronymium.parser
  (:require [acronymium.core :as acro]
            [instaparse.core :as insta]))

(def parser (insta/parser
              "<ruleset> = rule+
               rule = oper words
               <oper> = ('+'|'*') <#'\\s*'>
               <words> = (word <#'\\s*'>)+
               <word> = #'\\w+'"))

(def op-mapping {"+" :required
                 "*" :optional})

(defn parse-rule [_ op & words]
  [(get op-mapping op) words])

(defn add-rule [ruleset [op words]]
  (acro/add-rule ruleset op words))

(defn parse-ruleset [source]
  (let [tree      (parser source)
        translate (partial apply parse-rule)
        rules     (map translate tree)]
    (reduce add-rule (acro/new-ruleset) rules)))


