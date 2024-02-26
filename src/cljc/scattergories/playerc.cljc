(ns scattergories.playerc
  (:require [c3kit.bucket.api :as db]))

(defn ->player [nickname]
  {:kind     :player
   :nickname nickname})

(defn or-id [player-or-id]
  ((some-fn :id identity) player-or-id))

(defn by-nickname [nickname]
  (db/ffind-by :player :nickname nickname))