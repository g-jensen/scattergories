(ns scattergories.room
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.bucket.api :as db]
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

(defn create-room! [code]
  (let [code code
        room (roomc/->room code)]
    (db/tx room)))

(defn ws-create-room [{:keys [params] :as request}]
  (create-room! (unused-code))
  (apic/ok))

(defn maybe-missing-room [{:keys [room-code] :as params}]
  (when-not room-code (apic/fail nil "Missing room!")))
(defn maybe-missing-nickname [{:keys [nickname] :as params}]
  (when-not nickname (apic/fail nil "Missing nickname!")))

(defn join-room! [{:keys [nickname room-code]}]
  (let [player (db/tx (playerc/->player nickname))
        room   (db/ffind-by :room :code room-code)]
    (when (not (:host room))
      (db/tx (assoc room :host (:id player))))
    (apic/ok)))

(defn ws-join-room [{:keys [params] :as request}]
  (or (maybe-missing-room params)
      (maybe-missing-nickname params)
      (join-room! params)))