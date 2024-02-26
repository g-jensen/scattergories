(ns scattergories.schema.player
  (:require [c3kit.apron.schema :as s]))

(def player
  {:kind     (s/kind :player)
   :id       s/id
   :nickname {:type :string :validate s/present? :message "must be present"}
   })

(def all [player])