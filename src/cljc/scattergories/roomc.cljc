(ns scattergories.roomc
  (:require [c3kit.apron.utilc :as utilc]
            [c3kit.bucket.api :as db]
            [scattergories.playerc :as playerc]))

(defn ->room [code]
  {:kind    :room
   :code    code
   :players []})

(defn create-room! [code]
  (let [code code
        room (->room code)]
    (db/tx room)))

(defn add-player [{:keys [players] :as room} player]
  (let [id      (playerc/or-id player)
        players (conj players id)]
    (assoc room :players players)))

(defn join-room! [room player]
  (let [room   (add-player room player)]
    (if (not (:host room))
      (db/tx (assoc room :host (playerc/or-id player)))
      (db/tx room))))

(defn by-code [code]
  (db/ffind-by :room :code code))