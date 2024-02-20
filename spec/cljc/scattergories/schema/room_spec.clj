(ns scattergories.schema.room-spec
  (:require [c3kit.apron.schema :as schema]
            [scattergories.schema.room :as sut]
            [speclj.core :refer :all]))

(describe "room schema"

  (it "code is not nil"
    (should= "must be present"
      (->> {:kind :room} (schema/validate sut/room) schema/error-message-map :code))))