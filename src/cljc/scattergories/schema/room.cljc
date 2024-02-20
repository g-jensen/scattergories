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
  {:kind (s/kind :room)
   :id   s/id
   :code {:type :string :validate s/present? :message "must be present"}
   })

(def all [room])