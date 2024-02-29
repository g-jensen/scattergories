(ns scattergories.styles.layout.document
  (:refer-clojure :exclude [rem])
  (:require [scattergories.styles.core :refer :all]))

(def screen
(list

[:body :html
 {:margin 0
  :padding 0
  :font-family font-family
  :background-color "#f5f5dc"
  :color "#333"}]

[:h1
 {:color "#008080"
  :font-size "24px"
  :text-align "center"
  :margin-bottom "20px"}]

["input[type=\"text\"]"
 {:width "250px"
  :padding "10px"
  :margin "5px"
  :font-size "16px"
  :background-color "#faebd7"
  :color "#333"
  :border "1px solid #d2b48c"
  :border-radius "4px"
  :text-align "center"}]

[:button
 {:width "120px"
  :padding "10px"
  :background-color "#008080"
  :color "#fff"
  :font-size "16px"
  :border "none"
  :border-radius "5px"
  :cursor "pointer"
  :transition "background-color 0.3s"
  :margin "5px"}]

[:button:hover
 {:background-color "#006666"}]

[:ul
 {:list-style-type "none"
  :padding "0"}]

))
