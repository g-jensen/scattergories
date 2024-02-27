(ns scattergories.styles.layout.document
  (:refer-clojure :exclude [rem])
  (:require [scattergories.styles.core :refer :all]))

(def screen
(list

[:* :*:before :*:after {
  :box-sizing "border-box"
}]

["::selection" {
  :background-color primary
  :color white
}]

[:body :html {
  :width "100%"
  :height "100%"
}]

[:html {
  :font-size (px 16)
}]


[:body {
  :background-color white
  :color black
  :font-family font-family
  :line-height body-line-height
}]


))
