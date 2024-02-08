(ns scattergories.room-spec
  (:require [scattergories.room :as sut]
            [speclj.core :refer :all]))

(def idx (atom 5))

(describe "Room"
  (with-stubs)
  (before (reset! idx 5)
    (reset! sut/rooms {}))
  (redefs-around [rand-nth (stub :rand {:invoke (fn [coll]
                                                  (swap! idx inc)
                                                  (nth coll @idx))})])

  (context "room id"

    (it "random 6 numbers/letters"
      (should= "89ABCD" (sut/new-code)))

    (it "is always unique"
      (reset! sut/rooms {"89ABCD" nil})
      (should= "EFHJKL" (sut/new-code))))

  (context "ws-create-room"

    (it "adds room"
      (sut/ws-create-room [{}])
      (should-not (empty? @sut/rooms)))))