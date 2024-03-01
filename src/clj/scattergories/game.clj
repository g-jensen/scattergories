(ns scattergories.game
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.apron.time :as time]
            [c3kit.bucket.api :as db]
            [c3kit.wire.apic :as apic]
            [scattergories.categories :as categories]
            [scattergories.gamec :as gamec]
            [scattergories.playerc :as playerc]
            [scattergories.room :as room]
            [scattergories.roomc :as roomc]))

(defn start-round! [room]
  (let [room (-> (roomc/start room)
                 roomc/add-letter
                 (assoc :categories (take 12 (room/categories)))
                 (assoc :round-start (time/now)))]
    (db/tx room)))

(defn sleep! [ms] (Thread/sleep ms))
(def timeout 15000)

; TODO - be sure to set answers to [] on new round!

(defn all-submitted? [room]
  (let [players (ccc/map-all db/entity (:players room))]
    (every? #(some? (:answers %)) players)))

(defn -run-round [room timeout]
  (sleep! gamec/round-length)
  (let [start-time (time/now)]
    (while (and (not (all-submitted? room))
                (< (time/millis-between (time/now) start-time) timeout))
      (Thread/sleep 1000))
    (let [room    (assoc room :state :reviewing)
          players (ccc/map-all db/entity (:players room))
          answers (roomc/find-answers room)]
      (db/tx room)
      (room/push-to-room! room (cons room (concat players answers))))))
(defn run-round! [room timeout]
  (future (-run-round room timeout)))

(defn ws-start-game [{:keys [connection-id] :as request}]
  (let [player (playerc/by-conn-id connection-id)
        room (db/ffind-by :room :host (:id player))]
    (if room
      (let [room (start-round! room)]
        (room/push-room! room)
        (run-round! room timeout)
        (apic/ok room))
      (apic/fail nil "Only the host can start the game!"))))

(defn maybe-not-map? [{:keys [payload]}]
  (when (not (map? payload))
    (apic/fail nil "Answer payload must be a map!")))
(defn maybe-player-not-found [player]
  (when (not player)
    (apic/fail nil "Player not found!")))

(defn submit-answers! [player answers]
  (or (maybe-player-not-found player)
      (do (playerc/add-answers! player answers)
          (apic/ok))))

(defn ws-submit-answers [{:keys [payload connection-id] :as request}]
  (or (maybe-not-map? request)
      (let [player (playerc/by-conn-id connection-id)]
        (submit-answers! player payload))))