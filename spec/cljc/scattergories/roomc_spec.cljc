(ns scattergories.roomc-spec
  (:require [c3kit.apron.utilc :as utilc]
            [c3kit.bucket.api :as db]
            [scattergories.dark-souls :as ds :refer [depths laurentius frampt patches]]
            [scattergories.playerc :as playerc]
            [scattergories.roomc :as sut]
            [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context focus-it it should= should-not-be-nil]]))

(describe "roomc"
  (ds/init-with-schemas)

  (context "create-room!"

    (it "assigns code"
      (sut/create-room! ds/shrine-code)
      (should= ds/shrine-code (:code (db/ffind-by :room :code ds/shrine-code)))))

  (context "add-player"

    (it "to empty room"
      (let [room (sut/add-player {:players []} {:id 123})]
        (should= [123] (:players room))))

    (it "to room with one player"
      (let [room (sut/add-player {:players [123]} 124)]
        (should= [123 124] (:players room))))

    (it "to room with many players"
      (let [room (sut/add-player {:players [123 124]} 125)]
        (should= [123 124 125] (:players room)))))

  (context "join-room!"

    (it "first user to join becomes host"
      (let [response (sut/join-room! @depths @laurentius)]
        (should= @depths response)
        (should= (:id @laurentius) (:host @depths))))

    (it "subsequent users joining do not become host"
      (sut/join-room! @depths @laurentius)
      (let [crow     (playerc/create-player! "Giant Crow")
            response (sut/join-room! @depths crow)]
        (should= @depths response)
        (should= (:id @laurentius) (:host @depths))))

    (it "stores users who have joined in order"
      (sut/join-room! @depths @laurentius)
      (sut/join-room! @depths @frampt)
      (sut/join-room! @depths @patches)
      (should= (mapv :id [@laurentius @frampt @patches]) (:players @depths)))))