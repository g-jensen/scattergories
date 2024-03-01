(ns scattergories.game-spec
  (:require [c3kit.apron.utilc :as utilc]
            [c3kit.bucket.api :as db]
            [c3kit.bucket.spec-helperc :as helperc]
            [c3kit.wire.apic :as apic]
            [c3kit.wire.websocket :as ws]
            [scattergories.dark-souls :as ds :refer [firelink depths lautrec frampt patches]]
            [scattergories.dispatch :as dispatch]
            [scattergories.playerc :as playerc]
            [scattergories.game :as sut]
            [scattergories.room :as room]
            [scattergories.roomc :as roomc]
            [scattergories.schema.player :as player]
            [speclj.core :refer :all]))

(describe "Game"
  (with-stubs)
  (ds/init-with-schemas)

  (context "ws-start-game"

    (it "fails if connection-id is not host"
      (let [non-host-player (playerc/by-nickname "Patches")
            response (sut/ws-start-game {:connection-id (:conn-id non-host-player)})]
        (should= :fail (:status response))
        (should-be-nil (:payload response))
        (should= "Only the host can start the game!" (apic/flash-text response 0))))

    (it "succeeds if connection-id is host"
      (let [host-player (playerc/by-nickname "Lautrec")
            response (sut/ws-start-game {:connection-id (:id host-player)})]
        (should= :ok (:status response))
        (should= (assoc @ds/depths :state :started) (:payload response))))))
