(ns scattergories.room
  (:require [c3kit.bucket.api :as db]
            [scattergories.roomc :as roomc]))

(def rooms (atom {}))

(def code-chars
  (->> (concat (range 48 58) (range 65 91))
    (map char)
    (remove #{\O \0 \1 \I \G \g})))

(defn new-code []
  (->> (repeatedly #(rand-nth code-chars))
    (take 6)
    (apply str)))

(defn ws-create-room [{:keys [params] :as request}]
  (let [room (roomc/->room (new-code))]
    (db/tx room)))