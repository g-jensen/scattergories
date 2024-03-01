(ns scattergories.playerc_spec
  (:require [c3kit.bucket.api :as db]
            [scattergories.playerc :as sut]
            [scattergories.dark-souls :as ds :refer [lautrec]]
            [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context it should=]]))

(describe "playerc"
  (ds/init-with-schemas)

  (it "constructor"
    (let [player (sut/->player "Lautrec" "conn-id")]
      (should= "Lautrec" (:nickname player))
      (should= "conn-id" (:conn-id player))))

  (context "create-player!"

    (it "assigns nickname"
      (sut/create-player! "Solaire")
      (should= "Solaire" (:nickname (sut/by-nickname "Solaire"))))

    (it "assigns conn-id"
      (sut/create-player! "Solaire" "conn-solaire")
      (should= "conn-solaire" (:conn-id (sut/by-nickname "Solaire")))))

  (context "or-id"

    (it "player"
      (should= 123 (sut/or-id {:id 123})))

    (it "id"
      (should= 123 (sut/or-id 123))))

  (context "add-answers!"

    (it "no answers"
      (sut/add-answers! @lautrec {})
      (should= [] (:answers @lautrec)))

    (it "one answer"
      (sut/add-answers! @lautrec {"category1" "answer1"})
      (let [answer (db/ffind :answer)]
        (should= [(:id answer)] (:answers @lautrec))
        (should= (:id @lautrec) (:player answer))))

    (it "many answers"
      (sut/add-answers! @lautrec {"category1" "answer1"
                                  "category2" "answer2"
                                  "category3" "answer3"})
      (let [answers (db/find :answer)]
        (should= (map :id answers) (:answers @lautrec))))));