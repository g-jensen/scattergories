(ns scattergories.roomc-spec
  (:require [c3kit.apron.utilc :as utilc]
            [c3kit.bucket.api :as db]
            [scattergories.dark-souls :as ds]
            [scattergories.playerc :as playerc]
            [scattergories.roomc :as sut]
            [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context it should= should-not-be-nil]]))

(describe "roomc"
  (ds/init-with-schemas)

  (context "create-room!"

    (it "assigns code"
      (sut/create-room! ds/shrine-code)
      (should= ds/shrine-code (:code (db/ffind-by :room :code ds/shrine-code)))))

  (context "add-player"

    (it "to empty room"
      (let [room (sut/add-player {:players "[]"} {:id 123})]
        (should= [123] (utilc/<-edn (:players room)))))

    (it "to room with one player"
      (let [room (sut/add-player {:players "[123]"} 124)]
        (should= [123 124] (utilc/<-edn (:players room)))))

    (it "to room with many players"
      (let [room (sut/add-player {:players "[123 124]"} 125)]
        (should= [123 124 125] (utilc/<-edn (:players room))))))

  (context "join-room!"

    (it "first user to join becomes host"
      (sut/join-room! @ds/firelink "Fire Keeper")

      (let [player (playerc/by-nickname "Fire Keeper")
            room   (sut/by-code ds/shrine-code)]
        (should-not-be-nil player)
        (should= (:id player) (:host room))))

    (it "subsequent users joining do not become host"
      (sut/join-room! @ds/firelink "Solaire")
      (sut/join-room! @ds/firelink "Fire Keeper")
      (let [player (playerc/by-nickname "Solaire")
            room   (sut/by-code ds/shrine-code)]
        (should-not-be-nil player)
        (should= (:id player) (:host room))))

    (it "stores users who have joined in order"
      (sut/join-room! @ds/firelink "Solaire")
      (sut/join-room! @ds/firelink "Fire Keeper")
      (sut/join-room! @ds/firelink "Lautrec")
      (let [room        (sut/by-code ds/shrine-code)
            solaire     (playerc/by-nickname "Solaire")
            fire-keeper (playerc/by-nickname "Fire Keeper")
            lautrec     (playerc/by-nickname "Lautrec")]
        (should= (mapv :id [solaire fire-keeper lautrec]) (utilc/<-edn (:players room)))))))