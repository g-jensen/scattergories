(ns scattergories.playerc_spec
  (:require [scattergories.playerc :as sut]
            [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context it should=]]))

(describe "playerc"

  (context "or-id"

    (it "player"
      (should= 123 (sut/or-id {:id 123})))

    (it "id"
      (should= 123 (sut/or-id 123)))))