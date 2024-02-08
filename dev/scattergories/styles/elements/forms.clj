(ns scattergories.styles.elements.forms
  (:refer-clojure :exclude [rem])
  (:require [scattergories.styles.core :refer :all]))

(def screen
(list

[:fieldset {
  :margin-bottom size-minus-1
  }

  [:&.radio :&.checkbox {
    :margin [[size-plus-1 0]]
  }]

  [:&.validation-message-container
    [:input {
      :border-bottom-left-radius 0
      :border-bottom-right-radius 0
    }]

    [:.validation-message {
      :border-top-left-radius 0
      :border-top-right-radius 0
    }]
  ]
]

[:input :textarea ".select-menu > span" {
  :background-color white
  :border [[(px 1) "solid" light-grey]]
  :border-radius border-radius
  :color black
  :display "inline-block"
  :font-family (font-family "open-sans" "light")
  ; :height (px 50)
  :line-height body-line-height
  :padding [[size-minus-2 size-0 size-minus-2]]
  :position "relative"
  :width "100%"
  }

  [:&:focus {
    :box-shadow [[0 (px 0.5) (px 3) (px 3) primary]]
    :outline "none"
    :z-index 12
  }]

  ["&::placeholder" {
    :color light-grey
    :font-family (font-family "open-sans" "regular-italic")
  }]

  ["&::-webkit-input-placeholder" {
    :color light-grey
    :font-family (font-family "open-sans" "regular-italic")
  }]

  ["&::-moz-placeholder" {
    :color light-grey
    :font-family (font-family "open-sans" "regular-italic")
  }]

  [:&:-moz-placeholder {
    :color light-grey
    :font-family (font-family "open-sans" "regular-italic")
  }]

  [:&:-ms-input-placeholder {
    :color light-grey
    :font-family (font-family "open-sans" "regular-italic")
  }]

  [:&:disabled :&.disabled {
    :color light-grey
    :cursor "not-allowed"
  }]
]

[:button :.button {
  :background-color light-grey
  :border "none"
  :border-radius border-radius
  :color black
  :display "inline-block"
  :font-family (font-family "open-sans" "light")
  ; :height (px 50)
  :line-height size-plus-1
  :padding [[size-minus-2 size-0 size-minus-2]]
  :position "relative"
  :text-align "center"
  :text-decoration "none"
  :white-space "nowrap"
  }

  [:.fas {
    :font-size (em 0.85)
  }]

  [:&:focus {
    :box-shadow [[0 (px 0.5) (px 3) (px 3) primary]]
    :outline "none"
    :z-index 12
  }]

  [:&:hover {
    :background-color light-grey
    :color white
    :cursor "pointer"
    :text-decoration "none"
  }]

  [:&.primary {
    :background-color primary
    :color white
    }

    [:&:hover {
      :background-color primary
      :color white
    }]
  ]

  [:&:disabled :&.disabled {
    :background-color light-grey
    :border-color light-grey
    :color white
    }

    [:&:hover {
      :background-color light-grey
      :border-color light-grey
      :cursor "not-allowed"
    }]
  ]

  [:&.centered {
    :margin-left "auto"
    :margin-right "auto"
    :max-width (px 320)
    :text-align "center"
  }]

  [:&.full-width {
    :max-width "100%"
    :width "100%"
  }]

  [:&.large {
    :font-size size-plus-1
    :max-width (px 450)
    :padding [[size-minus-1 size-plus-2]]
  }]

  [:&.small {
    :height (px 34)
    :line-height size-0
    :padding [[size-minus-3 size-0]]
  }]
]

["input[type=checkbox]" "input[type=radio]" {
  :vertical-align "middle"
  :height "auto"
  :width "auto"
}]

[:label.inline-checkbox :label.inline-radio {
  :align-items "center"
  :display "flex"
  :gap size-minus-2
  }

  [:&:hover {
    :cursor "pointer"
  }]

  [:p {
    :font-size (rem 1)
    :text-transform "none"
  }]
]

[:.validation-message {
  :background-color light-grey
  :border-radius border-radius
  :color white
  :padding [[size-minus-1 size-0]]
  }

  [:&.error {
    :background-color error
  }]

  [:&.success {
    :background-color success
  }]
]

[:.flash-root {
  :position "fixed"
  :top (px 59)
  :left 0
  :width "auto"
  :z-index 15
}]

[:.flash-message {
  :background-color light-grey
  }

  [:&.error {
    :background-color error
    :color            white
  }]

  [:&.success {
    :background-color primary
    :color            white
  }]

  [:&.warn {
    :background-color warn
    :color            white
  }]

  [:.container {
    :padding [[(rem 0.5) (rem 1.3125)]]
  }]

  [:span {
    :margin-right (rem 1)
  }]

  [:span [:a {
    :display "inline"
    :padding 0
  }]]

  [:span:hover {
    :cursor "pointer"
  }]

  [:a {
    :color white
  }]

  [:a:hover {
    :color white
  }]
]

[:.site-spinner {
  :background-color primary
  :border-bottom-left-radius border-radius
  :border-bottom-right-radius border-radius
  :box-shadow [[(px 2) (px 2) (px 8) light-grey]]
  :position "fixed"
  :top 0
  :left "50%"
  :margin-left (px -50)
  :right 0
  :width (px 46)
  :height (px 30)
  :z-index 15
  }

  [:&:after {
    :background-image (str "url('/images/gifs/spinner-white.gif')")
    :background-repeat "no-repeat"
    :background-size [[size-plus-1 size-plus-1]]
    :content "''"
    :width size-plus-1
    :height size-plus-1
    :display "inline-block"
    :position "fixed"
    :left "calc(50% - 1rem)"
    :margin-left (px -22)
    :margin-top (px 4)
  }]
]

))
