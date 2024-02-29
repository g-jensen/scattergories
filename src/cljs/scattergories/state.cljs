(ns scattergories.state
  (:require [c3kit.bucket.api :as db]
            [reagent.core :as reagent]))

(def nickname (reagent/atom nil))
(def room (reagent/track #(db/ffind :room)))
(def players (reagent/track #(map db/entity (:players @room))))