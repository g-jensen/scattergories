(ns scattergories.dispatch
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.wire.websocket :as ws]
            [clojure.set :as set]))

(defn- push-to-connections! [conn-ids method data]
  (future
    (doseq [uid (set/intersection (ws/connected-ids) (set conn-ids))]
      (ws/push! uid method data))))

(defn push-to-player! [player method data]
  (push-to-connections! [(:conn-id player)] method data))

(defn push-to-players! [players method data]
  (push-to-connections! (map :conn-id players) method data))