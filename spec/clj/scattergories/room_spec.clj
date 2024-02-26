(ns scattergories.room-spec
  (:require [c3kit.bucket.api :as db]
            [c3kit.bucket.spec-helperc :as helperc]
            [c3kit.wire.apic :as apic]
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
      (should-not-be-nil (db/ffind-by :room :code "89ABCD")))

    (it "does not duplicate room codes"
      (db/tx (roomc/->room "89ABCD"))
      (sut/ws-create-room {})
      (should-not-be-nil (db/ffind-by :room :code "EFHJKL"))))

  (context "ws-join-room"

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
      (sut/create-room! "asylum")
      (let [response (sut/ws-join-room {:params {:nickname "Solaire" :room-code "asylum"}})]
        (should= :ok (:status response))
        (let [player (db/ffind-by :player :nickname "Solaire")
              room   (db/ffind-by :room :code "asylum")]
          (should-not-be-nil player)
          (should= (:id player) (:host room)))))

    (it "subsequent users joining do not become host"
      (sut/create-room! "asylum")
      (sut/join-room! {:nickname "Solaire" :room-code "asylum"})
      (sut/join-room! {:nickname "Fire Keeper" :room-code "asylum"})
      (let [player (db/ffind-by :player :nickname "Solaire")
            room   (db/ffind-by :room :code "asylum")]
        (should-not-be-nil player)
        (should= (:id player) (:host room))))))