(ns scattergories.state
  (:require [c3kit.bucket.api :as db]
            [reagent.core :as reagent]))

(def room (reagent/track #(db/ffind :room)))