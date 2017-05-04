# Acronymium

A Clojure program that can generate acronyms according to a set of rules specifying the words
 to use in different categories and whether a category is required or optional.

## Usage

Load core.clj in a REPL and:

`(word-of-length dict 6)`

Then re-define the word-classes to represent your own rules.

## Problems

I may have gone about this the wrong way, it seems like it needs a backtracking solution
as there are cases where it cannot make a match because of a non-optimal early choice of
word.

## License

Copyright © 2017 Matt Mower

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
