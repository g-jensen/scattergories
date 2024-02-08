(ns scattergories.styles.layout.mini-classes
  (:refer-clojure :exclude [rem])
  (:require [scattergories.styles.core :refer :all]))

(def screen
(list

[:.desktop {
  :display "block"
}]

[:.mobile {
  :display "none"
}]

[:hr {
  :background-color light-grey
  :border 0
  :height (px 2)
  :margin [[size-plus-1 0 size-plus-1]]
}]

[:.flex-1 {
  :flex 1
}]

[:.text-align-center {
  :display "block"
  :text-align "center"
}]

[:.text-align-left {
  :text-align "left"
}]

[:.text-align-right {
  :text-align "right"
}]

[:.text-inliner {
  :align-items "baseline"
  :display "flex"
  :gap size-minus-4
}]

[:.center-div {
  :display "block"
  :margin [[0 "auto"]]
}]

[:.flex-align-center-center {
  :display "flex"
  :align-items "center"
  :justify-content "center"
}]

(for [[size unit] {
  :minus-4 size-minus-4
  :minus-3 size-minus-3
  :minus-2 size-minus-2
  :minus-1 size-minus-1
  :0 size-0
  :plus-1 size-plus-1
  :plus-2 size-plus-2
  :plus-3 size-plus-3
  :plus-4 size-plus-4
  :plus-5 size-plus-5}]

  (list
    [(keyword (str ".margin-top-" (name size))) {:margin-top unit}]
    [(keyword (str ".margin-bottom-" (name size))) {:margin-bottom unit}]
    [(keyword (str ".margin-left-" (name size))) {:margin-left unit}]
    [(keyword (str ".margin-right-" (name size))) {:margin-right unit}]

    [(keyword (str ".padding-top-" (name size))) {:padding-top unit}]
    [(keyword (str ".padding-bottom-" (name size))) {:padding-bottom unit}]
    [(keyword (str ".padding-left-" (name size))) {:padding-left unit}]
    [(keyword (str ".padding-right-" (name size))) {:padding-right unit}]

    [(keyword (str ".gap-" (name size))) {:gap unit}]))

))
