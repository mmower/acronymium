# Acronymium

A Clojure program that can generate acronyms according to a set of rules specifying the words
 to use in different categories and whether a category is required or optional.

## Usage

Take a look at core-test.

Load in a REPL, define some rules using the parser.

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

## License

Copyright © 2017 Matt Mower

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
