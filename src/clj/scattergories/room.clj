(ns scattergories.room
  (:require [c3kit.bucket.api :as db]
            [c3kit.wire.apic :as apic]
            [scattergories.dispatch :as dispatch]
            [scattergories.playerc :as playerc]
            [scattergories.roomc :as roomc]))

(def lock (Object.))
(defmacro with-lock [& body]
  `(locking lock
     ~@body))

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
  (with-lock
    (roomc/create-room! (unused-code))
    (apic/ok)))

(defn maybe-missing-room [{:keys [room-code] :as params}]
  (when-not room-code (apic/fail nil "Missing room!")))
(defn maybe-nonexistent-room [room]
  (when-not room (apic/fail nil "Room does not exist!")))
(defn maybe-missing-nickname [{:keys [nickname] :as params}]
  (when-not nickname (apic/fail nil "Missing nickname!")))

(defn- create-and-join! [room nickname connection-id]
  (let [player (playerc/create-player! nickname connection-id)
        room   (roomc/join-room! room player)
        players (map db/entity (:players room))]
    (dispatch/push-to-players! players :room/update [room player])
    (apic/ok [room player])))

(defn- assign-to-room! [{:keys [room-code nickname]} connection-id]
  (let [room (db/ffind-by :room :code room-code)]
    (or (maybe-nonexistent-room room)
        (create-and-join! room nickname connection-id))))

(defn ws-join-room [{:keys [params connection-id] :as request}]
  (with-lock
    (or (maybe-missing-room params)
        (maybe-missing-nickname params)
        (assign-to-room! params connection-id))))