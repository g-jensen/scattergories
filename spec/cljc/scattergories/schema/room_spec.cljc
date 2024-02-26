(ns scattergories.schema.room-spec
  (:require [c3kit.apron.schema :as schema]
            [scattergories.schema.room :as sut]
            [speclj.core #?(:clj :refer :cljs :refer-macros) [describe it should=]]))

(describe "room schema"

  (it "code is required"
    (should= "must be present"
      (->> {:kind :room} (schema/validate sut/room) schema/error-message-map :code)))

  (it "players is required"
    (should= "must be present"
      (->> {:kind :room}
           (schema/validate sut/room)
           schema/error-message-map
           :players))))