(ns scattergories.room-spec
  (:require [c3kit.apron.utilc :as utilc]
            [c3kit.bucket.api :as db]
            [c3kit.bucket.spec-helperc :as helperc]
            [c3kit.wire.apic :as apic]
            [scattergories.playerc :as playerc]
            [scattergories.room :as sut]
            [scattergories.roomc :as roomc]
            [scattergories.schema.player :as player]
            [scattergories.schema.room :as room]
            [speclj.core :refer :all]))

(def idx (atom 5))

(describe "Room"
  (with-stubs)
  (helperc/with-schemas [room/room
                         player/player])
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

    (before (sut/create-room! "asylum"))

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

    (it "first user to join becomes host"
      (let [response (sut/ws-join-room {:params {:nickname "Solaire" :room-code "asylum"}})]
        (should= :ok (:status response))
        (let [player (playerc/by-nickname "Solaire")
              room   (roomc/by-code "asylum")]
          (should-not-be-nil player)
          (should= (:id player) (:host room)))))

    (it "subsequent users joining do not become host"
      (sut/join-room! {:nickname "Solaire" :room-code "asylum"})
      (sut/join-room! {:nickname "Fire Keeper" :room-code "asylum"})
      (let [player (playerc/by-nickname "Solaire")
            room   (roomc/by-code "asylum")]
        (should-not-be-nil player)
        (should= (:id player) (:host room))))

    (it "stores users who have joined in order"
      (sut/join-room! {:nickname "Solaire" :room-code "asylum"})
      (sut/join-room! {:nickname "Fire Keeper" :room-code "asylum"})
      (sut/join-room! {:nickname "Lautrec" :room-code "asylum"})
      (let [room        (roomc/by-code "asylum")
            solaire     (playerc/by-nickname "Solaire")
            fire-keeper (playerc/by-nickname "Fire Keeper")
            lautrec     (playerc/by-nickname "Lautrec")]
        (should= (mapv :id [solaire fire-keeper lautrec]) (utilc/<-edn (:players room)))))))