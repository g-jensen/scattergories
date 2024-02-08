(ns scattergories.styles.elements.media
  (:refer-clojure :exclude [rem])
  (:require [scattergories.styles.core :refer :all]))

(def screen
(list

[:img :video {
  :display "block"
  :max-width "100%"
}]

[:.object-fit-contain-container :.object-fit-cover-container {
  :overflow "hidden"
  :position "relative"
  }

  [:&.width-20 {
    :width (px 20)
    :height (px 20)
  }]

  [:img {
    :width "100%"
    :height "100%"
  }]
]

[:.object-fit-contain-container [:img {
  :object-fit "contain"
}]]

[:.object-fit-cover-container [:img {
  :object-fit "cover"
}]]

))
