(ns scattergories.schema.player
  (:require [c3kit.apron.schema :as s]))

(def answer
  {:kind     (s/kind :answer)
   :category {:type :string :validate s/present? :message "must be present"}
   :answer   {:type :string :validate s/present? :message "must be present"}})

(def player
  {:kind     (s/kind :player)
   :id       s/id
   :nickname {:type :string :validate s/present? :message "must be present"}
   :conn-id  {:type :string :message "must be a string"}
   :answers  {:type [answer]}
   })

(def all [answer player])