(ns acronymium.parser-test
  (:require [acronymium.parser :refer :all]
            [expectations :refer :all]
            [acronymium.core-test]))

(let [ruleset (parse-ruleset
                "+Strategy Plan
+Goals Ambition Objectives
+Purpose Vision
+Customer Value Proposition
+Delivery Execution Operations Process
*Perspective
*Technology Digital")]
  (expect acronymium.core-test/base-rules ruleset)
  )
