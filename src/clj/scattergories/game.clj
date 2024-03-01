(ns scattergories.game
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.apron.time :as time]
            [c3kit.bucket.api :as db]
            [c3kit.wire.apic :as apic]
            [scattergories.categories :as categories]
            [scattergories.playerc :as playerc]
            [scattergories.room :as room]
            [scattergories.roomc :as roomc]))

(defn start-round! [room]
  (let [room (-> (roomc/start room)
                 (assoc :categories (take 10 (roomc/categories)))
                 (assoc :round-start (time/now)))]
    (db/tx room)))

(defn ws-start-game [{:keys [connection-id] :as request}]
  (let [player (playerc/by-conn-id connection-id)
        room (db/ffind-by :room :host (:id player))]
    (if room
      (let [room (start-round! room)]
        (room/push-room! room)
        (apic/ok room))
      (apic/fail nil "Only the host can start the game!"))))