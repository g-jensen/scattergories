(ns scattergories.playerc
  (:require [c3kit.bucket.api :as db]))

(defn ->player
  ([nickname]
   {:kind     :player
    :nickname nickname})
  ([nickname conn-id]
   (merge (->player nickname)
          {:conn-id conn-id})))

(defn create-player!
  ([nickname] (db/tx (->player nickname)))
  ([nickname conn-id] (db/tx (->player nickname conn-id))))

(defn or-id [player-or-id]
  ((some-fn :id identity) player-or-id))

(defn by-nickname [nickname]
  (db/ffind-by :player :nickname nickname))