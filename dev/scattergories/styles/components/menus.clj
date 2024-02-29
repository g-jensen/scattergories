(ns scattergories.styles.components.menus
  (:refer-clojure :exclude [rem])
  (:require [scattergories.styles.core :refer :all]))

(def screen
(list

[:.homepage-container
 {:display "flex"
  :flex-direction "column"
  :align-items "center"
  :justify-content "center"
  :height "100vh"}]

[:.nickname-input :.room-actions
 {:margin-bottom "15px"}]

[:.room-actions
 {:display "flex"
  :justify-content "center"
  :gap "10px"}]

[:.main-container {
                   :display "grid"
                   :grid-template-columns "1fr 1fr 1fr"
                   }]

[:.left-container {
                   :float "right"
                   :text-align "right"
                   }]

[:.center {
           :display "flex"
           :justify-content "center"
           }]

[:.code-input {
               :max-width "100px"
               }]

[:.no-margin {
              :margin "0px"
              }]

[:.game-container
 {:padding "20px"
  :background-color "#fffaf0" ;; Creamy white background
  :border "2px solid #d2b48c" ;; Tan border
  :border-radius "8px"
  :box-shadow "0 2px 5px rgba(0,0,0,0.2)"
  :max-width "600px"
  :margin "20px"}] ;; Subtle shadow

[:.user-list
 {;:padding "20px"
  :background-color "#fffaf0" ;; Creamy white background
  :border "2px solid #d2b48c" ;; Tan border
  :border-radius "8px"
  :box-shadow "0 2px 5px rgba(0,0,0,0.2)"
  :width "200px" ;; Adjust width as needed
  :margin "20px"
  :padding "10px"
  }]

[:.categories
 {:display               "grid"
  :grid-template-columns "auto 1fr"
  :row-gap "10px"
  :column-gap "10px"}]

[:.categories-data
 {:font-size "18px"
  :color "#800000"
  :background-color "#fdf5e6" ;; Light, off-white background
  :padding "10px"
  :border "1px solid #faebd7" ;; Very light tan border
  :border-radius "5px"}]
))
