(ns scattergories.styles.elements.typography
  (:refer-clojure :exclude [rem])
  (:require [scattergories.styles.core :refer :all]))

(def screen
(list

[:h1 {
  :font-family font-family
  :font-size size-plus-4
  :line-height size-plus-4
}]

[:h2 {
  :font-family font-family
  :font-size size-plus-2
  :line-height size-plus-3
}]

[:h3 :legend {
  :font-family font-family
  :font-size size-plus-1
  :line-height size-plus-2
}]

[:h4 {
  :font-family font-family
  :font-size size-0
  :line-height body-line-height
  :text-transform "uppercase"
}]

[:h5 {
  :font-family font-family
  :font-size size-0
  :line-height body-line-height
}]

[:h6 :label :th {
  :font-family font-family
  :font-size (rem 0.75)
  :letter-spacing (px 0.5)
  :line-height (rem 0.85)
  :text-transform "uppercase"
  }

  [:span {
    :font-family font-family
    :font-size size-0
    :line-height body-line-height
    :text-transform "none"
  }]
]

[:h6 {
  :margin-bottom size-minus-3
}]

[:.small-caps {
  :font-size (em 0.75)
  :line-height (em 1.3125)
  :text-transform "uppercase"
}]

[:small {
  :display "inline-block"
  :font-size (em 0.75)
  :line-height (em 1.15)
}]

[:a {
  :color primary
  :text-decoration "none"
  }

  [:&:hover {
    :color primary
    :cursor "pointer"
    :text-decoration "underline"
  }]
]

[:b :strong {
  :font-family font-family
}]

[:small {
  :display "inline-block"
  :font-size (em 0.75)
  :line-height (em 1.15)
}]

[:.overflow-ellipsis
  overflow-ellipsis
]

[:.nowrap {
  :white-space "nowrap"
}]

))
