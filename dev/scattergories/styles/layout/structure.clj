(ns scattergories.styles.layout.structure
  (:refer-clojure :exclude [rem])
  (:require [scattergories.styles.core :refer :all]))

(def screen
(list

[:.container {
  :margin [[0 "auto"]]
  :position "relative"
  }

  [:&.width-300 {
    :max-width (px 300)
    :width "90%"
  }]

  [:&.width-750 {
    :max-width (px 750)
    :width "90%"
  }]

  [:&.full-width {
    :margin size-plus-1
  }]

  [:&.full-screen {
    :position "absolute"
    :top 0
    :bottom 0
    :left 0
    :right 0
  }]

  [:&.inset-0 {
    :padding size-0
  }]

  [:&.inset-plus-2 {
    :padding size-plus-2
  }]

  [:&.horizontal-inset-0 {
    :padding-left size-0
    :padding-right size-0
  }]

  [:&.horizontal-inset-plus-1 {
    :padding-left size-plus-1
    :padding-right size-plus-1
  }]

  [:&.vertical-scroll {
    :height "100%"
    :overflow-y "auto"
    :padding-left size-minus-4
    :padding-right size-minus-4
  }]

  [:&.horizontal-scroll {
    :width "100%"
    :overflow-x "auto"
  }]
]

[:.row {
  :display "flex"
  :position "relative"
  :width "100%"
}]

[:.column {
  :flex 1
  :min-width 0
  :position "relative"
  :width "100%"
  }

  [:&.width-300 {
    :max-width (px 300)
  }]
]

[:.inliner {
  :align-items "center"
  :display "flex"
  :flex [[0 1 "auto"]]
  :gap size-0
  :width "auto"
  }

  [:li {
    :flex-shrink 0
  }]

  [:&.space-between {
    :justify-content "space-between"
  }]
]

[:.flex-column {
  :align-items "center"
  :display "flex"
  :flex-direction "column"
  :gap size-0
}]

[:.fieldset-group {
  :display "flex"
  :gap size-minus-1
  }

  [:fieldset :div {
    :flex 1
    :position "relative"
  }]

  [:&.width-400 {
    :max-width (px 400)
  }]
]

[:.button-group {
  :align-items "center"
  :display "flex"
  :flex-wrap "wrap"
  :gap size-minus-1
  }

  [:button :.button {
    :display "inline-block"
    :width "auto"
  }]

  [:&.centered {
    :justify-content "center"
  }]

  [:&.full-width [:button :.button {
    :flex 1
  }]]
]

))
