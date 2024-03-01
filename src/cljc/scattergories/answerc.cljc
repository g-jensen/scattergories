(ns scattergories.answerc
  (:require [c3kit.bucket.api :as db]))

(defn ->answer [player-id [category answer]]
  {:kind     :answer
   :player   player-id
   :state    :accepted
   :category category
   :answer   answer})

(defn create-answer! [player-id category-pair]
  (db/tx (->answer player-id category-pair)))

(defn update-answer! [answer state]
  (db/tx (assoc answer :state state)))

(defn by-player [player]
  (db/find-by :answer :player (or (:id player) player)))