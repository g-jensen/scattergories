(ns scattergories.schema.room
  (:require [c3kit.apron.schema :as s]))

(def player
  {:kind   (s/kind :player)
   :name   {:type :string}
   :host?  {:type :boolean}
   :points {:type :long}})

(def categories
  {:kind (s/kind :list)
   :entries {:type [:string]}})

(def room
  {:kind    (s/kind :room)
   :players {:type [player]}
   :lists   {:type [categories]}})