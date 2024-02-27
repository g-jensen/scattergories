(ns scattergories.room
  (:require [c3kit.bucket.api :as db]
            [c3kit.wire.apic :as apic]
            [scattergories.playerc :as playerc]
            [scattergories.roomc :as roomc]))

(def code-chars
  (->> (concat (range 48 58) (range 65 91))
    (map char)
    (remove #{\O \0 \1 \I \G \g})))

(defn new-code []
  (->> (repeatedly #(rand-nth code-chars))
    (take 6)
    (apply str)))

(defn unused-code []
  (->> (repeatedly new-code)
       (remove #(db/ffind-by :room :code %))
       first))

(defn ws-create-room [{:keys [params] :as request}]
  (roomc/create-room! (unused-code))
  (apic/ok))

(defn maybe-missing-room [{:keys [room-code] :as params}]
  (when-not room-code (apic/fail nil "Missing room!")))
(defn maybe-nonexistent-room [room]
  (when-not room (apic/fail nil "Room does not exist!")))
(defn maybe-missing-nickname [{:keys [nickname] :as params}]
  (when-not nickname (apic/fail nil "Missing nickname!")))

(defn ws-join-room [{:keys [params] :as request}]
  (or (maybe-missing-room params)
      (maybe-missing-nickname params)
      (let [room (db/ffind-by :room :code (:room-code params))
            player (playerc/create-player! (:nickname params))]
        (or (maybe-nonexistent-room room)
            (->> [(roomc/join-room! room player) player]
                (apic/ok))))))