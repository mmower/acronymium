# Acronymium

A Clojure program that can generate acronyms according to a set of rules specifying the words
 to use in different categories and whether a category is required or optional.

## Usage

Load core.clj in a REPL and:

    (take 25 (words-of-length dict 6))
    =>
    (["PERSON" ["Plan" "Execution" "Return" "Service" "Objective" "Needs"]]
     ["COPPER" ["Customers" "Objective" "Plan" "Purpose" "Execution" "Return"]]
     ["SNAPER" ["Strategy" "Needs" "Ambition" "Purpose" "Execution" "Return"]]
     ["PARSON" ["Plan" "Ambition" "Return" "Service" "Operations" "Needs"]]
     ["PROVEN" ["Plan" "Return" "Objective" "Vision" "Execution" "Needs"]]
     ["COPPET" ["Customers" "Objective" "Plan" "Purpose" "Execution" "Technology"]]
     ["NAPPER" ["Needs" "Ambition" "Plan" "Purpose" "Execution" "Return"]]
     ["SAPOTA" ["Strategy" "Ambition" "Purpose" "Operations" "Technology" "Audience"]]
     ["ESCARP" ["Execution" "Strategy" "Customers" "Ambition" "Return" "Purpose"]]
     ["PSOCID" ["Plan" "Service" "Objective" "Customers" "Innovation" "Delivery"]]
     ["SOAPER" ["Strategy" "Objective" "Audience" "Purpose" "Execution" "Return"]]
     ["DOPPIA" ["Delivery" "Objective" "Plan" "Purpose" "Innovation" "Audience"]]
     ["POSNET" ["Plan" "Objective" "Service" "Needs" "Execution" "Technology"]]
     ["SPAVIN" ["Strategy" "Purpose" "Ambition" "Value" "Innovation" "Needs"]]
     ["CASPER" ["Customers" "Ambition" "Strategy" "Purpose" "Execution" "Return"]]
     ["APOSIA" ["Ambition" "Plan" "Operations" "Service" "Innovation" "Audience"]]
     ["GASPER" ["Goals" "Audience" "Strategy" "Purpose" "Execution" "Return"]]
     ["PAPPEA" ["Plan" "Ambition" "Purpose" "Proposition" "Execution" "Audience"]]
     ["SPRENG" ["Strategy" "Purpose" "Return" "Execution" "Needs" "Goals"]]
     ["COPPED" ["Customers" "Objective" "Plan" "Purpose" "Execution" "Digital"]]
     ["SEPTAN" ["Strategy" "Execution" "Purpose" "Technology" "Ambition" "Needs"]]
     ["CAPPIE" ["Customers" "Ambition" "Plan" "Purpose" "Innovation" "Execution"]]
     ["PIGPEN" ["Plan" "Innovation" "Goals" "Purpose" "Execution" "Needs"]]
     ["SVANTE" ["Strategy" "Vision" "Ambition" "Needs" "Technology" "Execution"]]
     ["VINOSE" ["Vision" "Innovation" "Needs" "Objective" "Strategy" "Execution"]])

Then try defining your own rules:

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
              
Each _word-class_ is a tuple of 3 items.
                       
* A purely descriptive keyword for the class
* A list of words for the class, exactly one of which must be selected for the class
* A keyword :must or :optional that specifies whether a word from the class must appear or only appears if its necessary to match a word.

## Problems

I may have gone about this the wrong way, it seems like it needs a backtracking solution
as there are cases where it cannot make a match because of a non-optimal early choice of
word.

For example with my rules we have a class `["Strategy" "Plan"]`. If we are trying to make
an acronym of a word beginning with *P* such as *"Prose"* it will always choose *"Plan"*
where there was another (possibly optional) word beginning with *P* that it could have
chosen, e.g. _"Proposition"_. Then, because you only select a single word from each class,
 _"Strategy"_ is no longer available to match the *S* that follows the *P*.

## License

Copyright © 2017 Matt Mower

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
