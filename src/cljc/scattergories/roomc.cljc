(ns scattergories.roomc
  (:require [c3kit.apron.utilc :as utilc]
            [c3kit.bucket.api :as db]
            [scattergories.playerc :as playerc]))

(defn ->room [code]
  {:kind    :room
   :code    code
   :players "[]"})

(defn add-player [{:keys [players] :as room} player]
  (let [id      (playerc/or-id player)
        players (-> (utilc/<-edn players)
                    (conj id)
                    utilc/->edn)]
    (assoc room :players players)))

(defn by-code [code]
  (db/ffind-by :room :code code))