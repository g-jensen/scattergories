(ns scattergories.room-spec
  (:require [c3kit.bucket.api :as db]
            [c3kit.bucket.spec-helperc :as helperc]
            [scattergories.room :as sut]
            [scattergories.schema.room :as room]
            [speclj.core :refer :all]))

(def idx (atom 5))

(describe "Room"
  (with-stubs)
  (helperc/with-schemas [room/room])
  (before (reset! idx 5)
    (reset! sut/rooms {}))
  (redefs-around [rand-nth (stub :rand {:invoke (fn [coll]
                                                  (swap! idx inc)
                                                  (nth coll @idx))})])

  (context "room id"

    (it "random 6 numbers/letters"
      (should= "89ABCD" (sut/new-code))))

  (context "ws-create-room"

    (it "saves room to db"
      (sut/ws-create-room {})
      (should-not-be-nil (db/ffind-by :room :code "89ABCD")))))