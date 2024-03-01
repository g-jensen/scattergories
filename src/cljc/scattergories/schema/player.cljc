(ns scattergories.schema.player
  (:require [c3kit.apron.schema :as s]))

(def answer
  {:kind     (s/kind :answer)
   :id       s/id
   :player   {:type :long :validate s/present? :message "must be present"}
   :state    {:type :keyword :validate s/present? :message "must be present"}
   :category {:type :string :validate s/present? :message "must be present"}
   :answer   {:type :string :validate s/present? :message "must be present"}})

(def player
  {:kind     (s/kind :player)
   :id       s/id
   :nickname {:type :string :validate s/present? :message "must be present"}
   :points   {:type :long}
   :conn-id  {:type :string :message "must be a string"}
   :answers  {:type [:long]}
   })

(def all [answer player])