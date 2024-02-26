(ns scattergories.schema.player-spec
  (:require [c3kit.apron.schema :as schema]
            [scattergories.schema.player :as sut]
            [speclj.core #?(:clj :refer :cljs :refer-macros) [describe it should=]]))

(describe "player schema"

  (it "nickname is required"
    (should= "must be present"
      (->> {:kind :room} (schema/validate sut/player) schema/error-message-map :nickname))))