(ns scattergories.push-handler
  (:require [c3kit.bucket.api :as db]
            [c3kit.wire.websocket :as ws]))

(defmethod ws/push-handler :room/update [push]
  (prn "push: " (:params push))
  (db/tx* (:params push)))