(ns scattergories.roomc-spec
  (:require [c3kit.apron.utilc :as utilc]
            [c3kit.bucket.api :as db]
            [scattergories.dark-souls :as ds :refer [firelink depths lautrec laurentius frampt patches]]
            [scattergories.playerc :as playerc]
            [scattergories.roomc :as roomc]
            [scattergories.roomc :as sut]
            [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context focus-it it should= should-not-contain
                                                              should-not-be-nil should-be-nil stub redefs-around with-stubs]]))
(def categories (mapv str (range 0 9)))

(describe "roomc"
  (with-stubs)
  (ds/init-with-schemas)

  (context "create-room!"

    (it "assigns code"
      (sut/create-room! ds/shrine-code)
      (should= ds/shrine-code (:code (db/ffind-by :room :code ds/shrine-code))))

    (it "sets initial state"
      (sut/create-room! ds/shrine-code)
      (should= :lobby (:state (db/ffind-by :room :code ds/shrine-code)))))

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

  (context "state"

    (it "start"
      (should= :started (:state (sut/start @firelink)))))

  (context "letter"
    (redefs-around [shuffle reverse])

    (it "picks random letter (excluding Q U V X Y Z)"
      (should= "W" (:letter (sut/add-letter @firelink)))))

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
      (should= (mapv :id [@laurentius @frampt @patches]) (:players @depths))))

  (context "remove-player"

    (it "from empty room"
      (let [room (sut/remove-player {:players []} {:id 123})]
        (should= [] (:players room))))

    (it "from room with one player"
      (let [room (sut/remove-player {:players [123]} 123)]
        (should= [] (:players room))))

    (it "from room with many players"
      (let [room (sut/remove-player {:players [123 124 125]} 123)]
        (should= [124 125] (:players room)))))

  (context "leave-room!"

    (it "removes player from room"
      (sut/leave-room! @firelink @patches)
      (should-not-contain (:id @patches) (:players @firelink)))

    (it "removes host if only one player"
      (sut/join-room! @depths @patches)
      (sut/leave-room! @depths @patches)
      (should-be-nil (:host @depths)))

    (it "sets host to next player if many"
      (sut/leave-room! @firelink @lautrec)
      (should= (:id @frampt) (:host @firelink))))

  (it "find-answers"
    (playerc/add-answers! @lautrec {"category1" "lautrec answer"})
    (playerc/add-answers! @patches {"category1" "patches answer"})
    (let [answers (sut/find-answers @firelink)]
      (should= ["lautrec answer" "patches answer"]
               (map :answer answers))))

  (it "finds room by player"
    (should= @firelink (roomc/by-player @lautrec))))