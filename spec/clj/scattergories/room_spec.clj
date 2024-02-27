(ns scattergories.room-spec
  (:require [c3kit.apron.utilc :as utilc]
            [c3kit.bucket.api :as db]
            [c3kit.bucket.spec-helperc :as helperc]
            [c3kit.wire.apic :as apic]
            [scattergories.dark-souls :as ds :refer [depths]]
            [scattergories.playerc :as playerc]
            [scattergories.room :as sut]
            [scattergories.roomc :as roomc]
            [scattergories.schema.player :as player]
            [scattergories.schema.room :as room]
            [speclj.core :refer :all]))

(def idx (atom 5))

(describe "Room"
  (with-stubs)
  (ds/init-with-schemas)
  (before (reset! idx 5))
  (redefs-around [rand-nth (stub :rand {:invoke (fn [coll]
                                                  (swap! idx inc)
                                                  (nth coll @idx))})])

  (context "room id"

    (it "random 6 numbers/letters"
      (should= "89ABCD" (sut/new-code))))

  (context "ws-create-room"

    (it "success"
      (should= (apic/ok) (sut/ws-create-room {})))

    (it "saves room to db"
      (sut/ws-create-room {})
      (should-not-be-nil (roomc/by-code "89ABCD")))

    (it "does not duplicate room codes"
      (db/tx (roomc/->room "89ABCD"))
      (sut/ws-create-room {})
      (should-not-be-nil (roomc/by-code "EFHJKL"))))

  (context "ws-join-room"

    (before (roomc/create-room! "asylum"))

    (it "missing room"
      (let [response (sut/ws-join-room {:params {:nickname "Solaire"}})]
        (should= :fail (:status response))
        (should-be-nil (:payload response))
        (should= "Missing room!" (apic/flash-text response 0))))

    (it "missing nickname"
      (let [response (sut/ws-join-room {:params {:room-code "asylum"}})]
        (should= :fail (:status response))
        (should-be-nil (:payload response))
        (should= "Missing nickname!" (apic/flash-text response 0))))

    ; TODO - do not create player
    (it "room does not exist"
      (let [response (sut/ws-join-room {:params {:nickname "Solaire" :room-code "parish"}})]
        (should= :fail (:status response))
        (should-be-nil (:payload response))
        (should= "Room does not exist!" (apic/flash-text response 0))))

    (it "joins room"
      (let [response (sut/ws-join-room {:params {:nickname "Sewer Rat" :room-code ds/depths-code}})]
        (should= :ok (:status response))
        (let [player (playerc/by-nickname "Sewer Rat")]
          (should-not-be-nil player)
          (should= [@depths player] (:payload response))
          (should= (:id player) (:host @ds/depths)))))

    (it "notifies players of new room state"
      )))