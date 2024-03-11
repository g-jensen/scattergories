(ns scattergories.roomc
  (:require [c3kit.apron.utilc :as utilc]
            [c3kit.apron.corec :as ccc]
            [c3kit.bucket.api :as db]
            [scattergories.playerc :as playerc]))

(defn ->room [code]
  {:kind    :room
   :code    code
   :state   :lobby
   :players []})

(defn create-room! [code]
  (let [code code
        room (->room code)]
    (db/tx room)))

(defn add-player [{:keys [players] :as room} player]
  (let [id      (playerc/or-id player)
        players (conj players id)]
    (assoc room :players players)))

(defn start [room]
  (assoc room :state :started))

(def letters ["A" "B" "C" "D" "E" "F" "G" "H" "I" "J" "K" "L" "M" "N" "O" "P" "R" "S" "T" "W"])
(defn add-letter [room]
  (let [letter (first (shuffle letters))]
    (assoc room :letter letter)))

(defn next-category-idx [room]
  (if (:category-idx room)
    (if (< (:category-idx room) (dec (count (:categories room))))
      (update room :category-idx inc)
      (dissoc room :category-idx))
    (assoc room :category-idx 0)))

(defn remove-player [{:keys [players] :as room} player]
  (let [id (playerc/or-id player)
        players (remove #{id} players)]
    (assoc room :players players)))

(defn join-room! [room player]
  (let [room   (add-player room player)]
    (if (not (:host room))
      (db/tx (assoc room :host (playerc/or-id player)))
      (db/tx room))))

(defn leave-room! [room player]
  (let [room (remove-player room player)
        host (first (:players room))
        room (assoc room :host host)]
    (db/tx room)))

(defn or-id [player-or-id]
  ((some-fn :id identity) player-or-id))

(defn find-answers [room]
  (let [room-id (or-id room)
        room    (db/entity room-id)
        players (map db/entity (:players room))
        answer-ids (->> (map :answers players)
                        flatten
                        (remove nil?))]
    (mapv db/entity answer-ids)))

(defn by-code [code]
  (db/ffind-by :room :code code))
(defn by-player [player]
  (db/ffind :room :where {:players [(playerc/or-id player)]}))