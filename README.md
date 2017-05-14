# Acronymium

A Clojure program for generating acronyms.

In particular what Acronymium does is allow you to specify rules that determine the
way specified words can be combined to form an acronym. For example if you want the
word _Ham_ to be part of the acronym you can do that.

The principle use is to specify a set of categories. A category is a rule and a set of
words (that are, in principle, interchangeable) one of which should appear in the
final acronym.
 
The main use is to create categories that define the "shape" of the acronyms you want
and then fire the dictionary through them. This will result in familiar words that
are defined in terms of words you want to use.

## Usage

Take a look at `core-test` and `parser-test`.

Load in a REPL, define some word categories using the parser. A category is defined
by a rule `+` or `*` followed by one or more words. You can have as many categories as you
like.

The rule `+` means _must have one of_ while `*` means _can
have one of_, so `+Ham Bacon Sausage` means the resulting acronym must contain
either an _H_, _B_, or _S_ and where that letter appears the corresponding
word will be used. While `*Eggs` means the word can optionally have an _E_ if it
needs one.

So `+` defines constraints while `*` creates the opportunity to use other
letters which makes a wider range of words open to being turned into acronyms.

Acronyms will, by definition, have a minimum length the same as the number of
`+` categories since one letter/word from each must appear.

    (require '[acronymium.core :refer [acronyms]]
              [acronymium.parser :refer [parse-ruleset]])
              
    (def ruleset (parse-ruleset "+Ham Bacon Sausage
                                 +Tomatoes Mushrooms
                                 *Waffles Chips
                                 *Eggs
                                 *Beans Ketchup
                                 *Tea Coffee"))
    (acronyms ruleset "hem")
    => (["Ham" "Eggs" "Mushrooms"])
    (acronyms ruleset "bet")
    => (["Bacon" "Eggs" "Tomatoes"])
    (acronyms ruleset "wet")
    => ()
    
Now feed a dictionary to `acronyms` and find words you like with
a meaningful acronym.

    (def dict (load-dict "/usr/share/dict/words"))
    (def adict (acro-dict ruleset dict))
    adict
    =>
    {"TECH" (["Tomatoes" "Eggs" "Chips" "Ham"] ["Tomatoes" "Eggs" "Coffee" "Ham"]),
     "STET" (["Sausage" "Tomatoes" "Eggs" "Tea"] ["Sausage" "Tea" "Eggs" "Tomatoes"]),
     "BETH" (["Beans" "Eggs" "Tomatoes" "Ham"]),
     "SET" (["Sausage" "Eggs" "Tomatoes"]),
     "BEST" (["Beans" "Eggs" "Sausage" "Tomatoes"]),
     "HEWT" (["Ham" "Eggs" "Waffles" "Tomatoes"]),
     "STEW" (["Sausage" "Tomatoes" "Eggs" "Waffles"]),
     "SMEW" (["Sausage" "Mushrooms" "Eggs" "Waffles"]),
     "KHET" (["Ketchup" "Ham" "Eggs" "Tomatoes"]),
     "CEST" (["Chips" "Eggs" "Sausage" "Tomatoes"] ["Coffee" "Eggs" "Sausage" "Tomatoes"]),
     "TCHE" (["Tomatoes" "Chips" "Ham" "Eggs"] ["Tomatoes" "Coffee" "Ham" "Eggs"]),
     "WHET" (["Waffles" "Ham" "Eggs" "Tomatoes"]),
     "TCH" (["Tomatoes" "Chips" "Ham"] ["Tomatoes" "Coffee" "Ham"]),
     "BET" (["Bacon" "Eggs" "Tomatoes"]),
     "TST" (["Tomatoes" "Sausage" "Tea"] ["Tea" "Sausage" "Tomatoes"]),
     "SECT" (["Sausage" "Eggs" "Chips" "Tomatoes"] ["Sausage" "Eggs" "Coffee" "Tomatoes"]),
     "HEM" (["Ham" "Eggs" "Mushrooms"]),
     "THE" (["Tomatoes" "Ham" "Eggs"]),
     "KETCH" (["Ketchup" "Eggs" "Tomatoes" "Chips" "Ham"] ["Ketchup" "Eggs" "Tomatoes" "Coffee" "Ham"]),
     "TEST" (["Tomatoes" "Eggs" "Sausage" "Tea"] ["Tea" "Eggs" "Sausage" "Tomatoes"]),
     "TETCH" (["Tomatoes" "Eggs" "Tea" "Chips" "Ham"] ["Tea" "Eggs" "Tomatoes" "Chips" "Ham"]),
     "SETT" (["Sausage" "Eggs" "Tomatoes" "Tea"] ["Sausage" "Eggs" "Tea" "Tomatoes"]),
     "TH" (["Tomatoes" "Ham"]),
     "KEMB" (["Ketchup" "Eggs" "Mushrooms" "Bacon"]),
     "HET" (["Ham" "Eggs" "Tomatoes"]),
     "ETCH" (["Eggs" "Tomatoes" "Chips" "Ham"] ["Eggs" "Tomatoes" "Coffee" "Ham"]),
     "ST" (["Sausage" "Tomatoes"]),
     "STEM" (["Sausage" "Tea" "Eggs" "Mushrooms"]),
     "THEB" (["Tomatoes" "Ham" "Eggs" "Beans"]),
     "WEST" (["Waffles" "Eggs" "Sausage" "Tomatoes"]),
     "THEM" (["Tea" "Ham" "Eggs" "Mushrooms"]),
     "CHET" (["Chips" "Ham" "Eggs" "Tomatoes"] ["Coffee" "Ham" "Eggs" "Tomatoes"]),
     "WECHT" (["Waffles" "Eggs" "Coffee" "Ham" "Tomatoes"]),
     "TETH" (["Tomatoes" "Eggs" "Tea" "Ham"] ["Tea" "Eggs" "Tomatoes" "Ham"]),
     "MES" (["Mushrooms" "Eggs" "Sausage"]),
     "THEW" (["Tomatoes" "Ham" "Eggs" "Waffles"])}

## Notes

The parser was built using the fabulous [Instaparse](https://github.com/Engelberg/instaparse)
by Mark Engelberg. Tests use [expectations](https://github.com/clojure-expectations/expectations).

## License

Copyright © 2017 Matt Mower

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
