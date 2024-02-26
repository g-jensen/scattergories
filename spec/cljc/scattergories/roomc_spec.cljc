(ns scattergories.roomc-spec
  (:require [c3kit.apron.utilc :as utilc]
            [scattergories.playerc :as playerc]
            [scattergories.roomc :as sut]
            [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context it should=]]))

(describe "roomc"

  (context "add-player"

    (it "to empty room"
      (let [room (sut/add-player {:players "[]"} {:id 123})]
        (should= [123] (utilc/<-edn (:players room)))))

    (it "to room with one player"
      (let [room (sut/add-player {:players "[123]"} 124)]
        (should= [123 124] (utilc/<-edn (:players room)))))

    (it "to room with many players"
      (let [room (sut/add-player {:players "[123 124]"} 125)]
        (should= [123 124 125] (utilc/<-edn (:players room)))))))