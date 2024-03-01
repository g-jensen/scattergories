(ns scattergories.schema.full
  (:require [scattergories.schema.room :as room]
            [scattergories.schema.player :as player]))

(def full-schema
  [room/room
   player/answer
   player/player])
