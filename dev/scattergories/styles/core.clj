(ns scattergories.styles.core
  (:refer-clojure :exclude [rem])
  (:require [garden.units :as units]
            [garden.color :as color]))

(defn px [n] (units/px n))
(defn em [n] (units/em n))
(defn rem [n] (units/rem n))
(defn percent [n] (units/percent n))

(def black (color/rgb 25 28 38))
(def dark-grey (color/rgb 60 60 60))
(def medium-grey (color/rgb 120 120 120))
(def light-grey (color/rgb 217 217 217))
(def white (color/rgb 255 255 255))
(def primary (color/rgb 71 140 202))
(def success (color/rgb 71 202 154))
(def error (color/rgb 202 83 71))
(def warn (color/rgb 242 186 77))

(def size-minus-4 "0.25rem")
(def size-minus-3 "0.375rem")
(def size-minus-2 "0.5rem")
(def size-minus-1 "0.625rem")
(def size-0 "1rem")
(def size-plus-1 "1.3125rem")
(def size-plus-2 "1.8125rem")
(def size-plus-3 "2.625rem")
(def size-plus-4 "3.975rem")
(def size-plus-5 "6rem")

(def border-radius "2px")

(def body-line-height "1.7rem")

(def overflow-ellipsis {
  :overflow      "hidden"
  :text-overflow "ellipsis"
  :white-space   "nowrap"
  :width         "100%"
})

(defn font-family [face weight]
  (str "'" face "-" weight "', Helvetica, sans-serif"))

(defn font-load [face weight]
  (list 
    ["@font-face" {
      :font-family (str "'" face "-" weight "'")}
      {:src        (str "url('/fonts/" face "-" weight ".woff2') format('woff2'), "
                        "url('/fonts/" face "-" weight ".woff') format('woff')")
      :font-weight "normal"
      :font-style  "normal"
    }]

    ["@font-face" {
      :font-family (str "'" face "-" weight "-italic'")}
      {:src        (str "url('/fonts/" face "-" weight "-italic.woff2') format('woff2'), "
                        "url('/fonts/" face "-" weight "-italic.woff') format('woff')")
      :font-weight "normal"
      :font-style  "normal"
    }]
  )
)

(def fonts 
  (map #(font-load "open-sans" %) ["bold" "extrabold" "light" "regular" "semibold"]))
