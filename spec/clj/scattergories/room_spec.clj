(ns scattergories.room-spec
  (:require [c3kit.apron.utilc :as utilc]
            [c3kit.bucket.api :as db]
            [c3kit.bucket.spec-helperc :as helperc]
            [c3kit.wire.apic :as apic]
            [c3kit.wire.websocket :as ws]
            [scattergories.dark-souls :as ds :refer [firelink depths lautrec frampt patches]]
            [scattergories.dispatch :as dispatch]
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
      (let [response (sut/ws-create-room {})
            room     (roomc/by-code "89ABCD")]
        (should= :ok (:status response))
        (should= ["89ABCD"] (:payload response))))

    (it "does not duplicate room codes"
      (db/tx (roomc/->room "89ABCD"))
      (sut/ws-create-room {})
      (should-not-be-nil (roomc/by-code "EFHJKL"))))

  (context "ws-join-room"
    (redefs-around [dispatch/push-to-players! (stub :push-to-players!)])

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

    (it "room does not exist"
      (let [response (sut/ws-join-room {:params {:nickname "Solaire" :room-code "parish"}})]
        (should= :fail (:status response))
        (should-be-nil (:payload response))
        (should= "Room does not exist!" (apic/flash-text response 0))
        (should-be-nil (playerc/by-nickname "Solaire"))))

    (it "joins room"
      (let [response (sut/ws-join-room {:params        {:nickname "Sewer Rat" :room-code ds/depths-code}
                                        :connection-id "conn-rat"})]
        (should= :ok (:status response))
        (let [player (playerc/by-nickname "Sewer Rat")]
          (should-not-be-nil player)
          (should= [@depths player] (:payload response))
          (should= (:id player) (:host @ds/depths))
          (should= "conn-rat" (:conn-id player)))))

    (it "notifies players of new room state"
      (let [response (sut/ws-join-room {:params        {:nickname "Giant Crow" :room-code ds/shrine-code}
                                        :connection-id "conn-crow"})
            crow     (playerc/by-nickname "Giant Crow")]
        (should= :ok (:status response))
        (should-have-invoked :push-to-players! {:with [(map db/entity (:players @firelink))
                                                       :room/update
                                                       [@firelink crow]]})))

    (it "responds with current room state & all current players"
      (let [response (sut/ws-join-room {:params        {:nickname "Giant Crow" :room-code ds/shrine-code}
                                        :connection-id "conn-crow"})
            crow     (playerc/by-nickname "Giant Crow")]
        (should= :ok (:status response))
        (should= (set [@firelink crow @lautrec @frampt @patches]) (set (:payload response))))))

  (context "ws-leave-room"
    (redefs-around [dispatch/push-to-players! (stub :push-to-players!)])

    (it "removes player from room"
      (sut/ws-leave-room {:connection-id "conn-patches"})
      (should-not-contain (:id @patches) (:players @firelink))
      (should= (mapv :id [@lautrec @frampt]) (:players @firelink)))

    (it "notifies players of new room state"
      (sut/ws-leave-room {:connection-id "conn-patches"})
      (should-have-invoked :push-to-players! {:with [(map db/entity (:players @firelink))
                                                     :room/update
                                                     [@firelink]]}))))