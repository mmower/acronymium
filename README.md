# Acronymium

A Clojure program that can generate acronyms according to a set of rules specifying the words
 to use in different categories and whether a category is required or optional.

## Usage

Take a look at core-test.

Load in a REPL, define some rules

    (def rules (-> (new-rules)
                   (must-have-one-of ["Ham" "Bacon"])
                   (must-have-one-of ["Tomatoes" "Mushrooms"])
                   (can-have-one-of ["Eggs" "Beans"])))
    (acronyms rules "hem")
    => (["Ham" "Eggs" "Mushrooms"])
    
Now feed a dictionary to `acronyms` and find words you like with
a meaningful acronym.

    (def dict (load-dict "/usr/share/dict/words"))
    (def adict (acro-dict rules dict))
        
## License

Copyright © 2017 Matt Mower

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
