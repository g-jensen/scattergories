(ns scattergories.game
  (:require [c3kit.bucket.api :as db]
            [c3kit.wire.apic :as apic]
            [scattergories.playerc :as playerc]
            [scattergories.room :as room]))

(defn ws-start-game [{:keys [connection-id] :as request}]
  (let [player (playerc/by-conn-id connection-id)
        room (db/ffind-by :room :host (:id player))]
    (if room
      (apic/ok (assoc room :state :started))
      (apic/fail nil "Only the host can start the game!"))))