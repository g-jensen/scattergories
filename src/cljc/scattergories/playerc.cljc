(ns scattergories.playerc
  (:require [c3kit.apron.schema :as schema]
            [c3kit.bucket.api :as db]
            [scattergories.answerc :as answerc]
            [scattergories.schema.player :as player]))

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

(defn add-answers! [player answers]
  (let [answers (map answerc/->answer answers)]
    (db/tx (assoc player :answers answers))))

(defn or-id [player-or-id]
  ((some-fn :id identity) player-or-id))

(defn by-nickname [nickname]
  (db/ffind-by :player :nickname nickname))
(defn by-conn-id [conn-id]
  (db/ffind-by :player :conn-id conn-id))