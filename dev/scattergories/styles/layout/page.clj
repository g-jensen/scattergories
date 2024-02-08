(ns scattergories.styles.layout.page
  (:refer-clojure :exclude [rem])
  (:require [scattergories.styles.core :refer :all]))

(def screen
(list

[:#app-root :#content {
  :height "100%"
}]

[:#content {
  :display "flex"
  :flex-direction "column"
  :min-height "100vh"
  :height "auto"
  :width "100%"
  :outline "none"
}]

[:header {
  :border-bottom [[(px 1) "solid" light-grey]]
  }

  [:.logo {
    :height (px 44)
  }]

  [:.logo:hover {
    :opacity 0.6
  }]
]

[:main {
  :flex 1
}]

[:section {
  :position "relative"
  :width "100%"
}]

))
