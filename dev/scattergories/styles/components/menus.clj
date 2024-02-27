(ns scattergories.styles.components.menus
  (:refer-clojure :exclude [rem])
  (:require [scattergories.styles.core :refer :all]))

(def screen
(list

[:.user-menu-container {
  :height (px 58)
  :position "relative"
  :text-align "right"
}]

[:.user-menu-toggle {
  :align-items "center"
  :display "flex"
  :flex-direction "column"
  :font-size size-plus-1
  :height "100%"
  :justify-content "center"
  :padding-left size-plus-1
  :padding-right size-plus-1
  }

  [:&:hover :&.active {
    :background-color primary
    :border-color primary
    :color white
    :cursor "pointer"
  }]
]

[:.user-menu {
  :position "absolute"
  :top (px 58)
  :right (px 0)
  :width (px 220)
  }

  [:a {
    :align-items "center"
    :display "flex"
    :font-family font-family
    :flex [[0 1 "auto"]]
    :gap size-minus-2
    :width "auto"
    :color black
    :padding [[size-minus-3 size-0]]
    :text-decoration "none"
  }]

  [:.fa-solid {
    :text-align "center"
    :width (px 18)
  }]

  [:ul {
    :background-color white
    :border [[(px 1) "solid" light-grey]]
    :border-radius border-radius
    :box-shadow [[0 (px 3) (px 8) light-grey]]
    :position "absolute"
    :left 0
    :text-align "left"
    :width "100%"
    :z-index 9
  }]

  [:li {
    :background-color white
    :color black
    :padding [[size-minus-1 size-0]]
    :text-align "left"
  }]

  [:li:first-child {
    :border-top-left-radius border-radius
    :border-top-right-radius border-radius
  }]

  [:li:last-child {
    :border-bottom-left-radius border-radius
    :border-bottom-right-radius border-radius
  }]

  [:li.highlight :li:hover {
    :background-color primary
    :color white
    :cursor "pointer"
    }

    [:a {
      :color white
    }]
  ]

  [:li.header {
    :padding-bottom 0
  }]

  [:li.header:hover {
    :background-color white
    :color            black
    :cursor           "auto"
  }]

  [:li.disabled [:a {
    :color dark-grey
    :cursor "not-allowed"
  }]]

  [:li.disabled:hover {
    :background-color white
  }]
  
  [:li.disabled:hover [:a {
    :color dark-grey
  }]]

  [:hr {
    :margin 0
  }]

  [:label {
    :font-family    font-family
    :font-size      size-0
    :line-height    body-line-height
    :text-transform "none"
  }]
]

))
