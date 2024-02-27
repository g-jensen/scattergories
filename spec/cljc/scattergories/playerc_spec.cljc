(ns scattergories.playerc_spec
  (:require [scattergories.playerc :as sut]
            [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context it should=]]))

(describe "playerc"

  (it "constructor"
    (let [player (sut/->player "Lautrec" "conn-id")]
      (should= "Lautrec" (:nickname player))
      (should= "conn-id" (:conn-id player))))

  (context "or-id"

    (it "player"
      (should= 123 (sut/or-id {:id 123})))

    (it "id"
      (should= 123 (sut/or-id 123)))))