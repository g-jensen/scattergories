(ns scattergories.schema.room
  (:require [c3kit.apron.schema :as s]))

(def room
  {:kind        (s/kind :room)
   :id          s/id
   :code        {:type :string :validate s/present? :message "must be present"}
   :host        {:type :long}
   :players     {:type [:long] :validate s/present? :message "must be present"}

   :state       {:type :keyword :validate s/present? :message "must be present"}

   :round-start {:type :instant}
   :letter      {:type :string}
   :categories  {:type [:string]}
   })

(def all [room])