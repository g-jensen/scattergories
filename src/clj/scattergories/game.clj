(ns scattergories.game
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.apron.time :as time]
            [c3kit.bucket.api :as db]
            [c3kit.wire.apic :as apic]
            [scattergories.answerc :as answerc]
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
    (let [room    (assoc room :state :reviewing
                              :category-idx 0)
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

(defn maybe-not-map? [{:keys [params]}]
  (when (not (map? params))
    (apic/fail nil "Answer payload must be a map!")))
(defn maybe-player-not-found [player]
  (when (not player)
    (apic/fail nil "Player not found!")))

(defn submit-answers! [player answers]
  (or (maybe-player-not-found player)
      (do (playerc/add-answers! player answers)
          (apic/ok))))

(defn ws-submit-answers [{:keys [params connection-id] :as request}]
  (or (maybe-not-map? request)
      (let [player (playerc/by-conn-id connection-id)]
        (submit-answers! player params))))

(defn maybe-invalid-state [{:keys [state] :as params}]
  (when-not (contains? #{:accepted :bonus :declined} state)
    (apic/fail nil "Invalid answer state!")))
(defn maybe-answer-not-found [answer]
  (when-not answer
    (apic/fail nil "Answer not found!")))

(defn update-answer! [room answer {:keys [state] :as params}]
  (or (maybe-answer-not-found answer)
      (let [answer (answerc/update-answer! answer state)]
        (room/push-to-room! room [answer])
        (apic/ok))))

(defn ws-update-answer [{:keys [params connection-id] :as request}]
  (or (maybe-invalid-state params)
      (let [answer (db/entity (:answer-id params))
            player (playerc/by-conn-id connection-id)
            room   (db/ffind-by :room :host (:id player))]
        (update-answer! room answer params))))

(def point-fns {:accepted inc
                :declined identity
                :bonus    (comp inc inc)})

(defn assign-points! [answer]
  (let [player (db/entity (:player answer))]
    (when player
      (db/tx (update player :points (get point-fns (:state answer)))))))

(defn maybe-remove-answers [room players]
  (when (= :lobby (:state room))
    (let [answers (flatten (map :answers players))]
      (doseq [answer answers] (db/delete :answer answer)))
    (db/tx* (map #(dissoc % :answers) players))))

(defn ws-next-category [{:keys [connection-id] :as request}]
  (let [player (playerc/by-conn-id connection-id)
        room   (roomc/by-player player)
        category (nth (:categories room) (:category-idx room))
        answers (->> room :players (map db/entity) (map :answers) (map #(map db/entity %))
                     (map #(first (filter (fn [ans] (= category (:category ans))) %))))
        room   (roomc/next-category-idx room)
        room   (if (:category-idx room) room (assoc room :state :lobby))
        players (map db/entity (:players room))]
    (doseq [answer answers]
      (assign-points! answer))
    (maybe-remove-answers room players)
    (room/push-to-room! room (cons room (map db/entity (:players room))))
    (apic/ok (db/tx room))))