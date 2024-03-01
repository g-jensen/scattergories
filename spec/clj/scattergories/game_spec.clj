(ns scattergories.game-spec
  (:require [c3kit.apron.time :as time]
            [c3kit.apron.utilc :as utilc]
            [c3kit.bucket.api :as db]
            [c3kit.bucket.spec-helperc :as helperc]
            [c3kit.wire.apic :as apic]
            [c3kit.wire.websocket :as ws]
            [scattergories.answerc :as answerc]
            [scattergories.categories :as categories]
            [scattergories.dark-souls :as ds :refer [firelink depths lautrec frampt patches]]
            [scattergories.dispatch :as dispatch]
            [scattergories.playerc :as playerc]
            [scattergories.game :as sut]
            [scattergories.room :as room]
            [scattergories.roomc :as roomc]
            [scattergories.schema.player :as player]
            [speclj.core :refer :all]))

(describe "Game"
  (with-stubs)
  (ds/init-with-schemas)

  (context "ws-start-game"
    (redefs-around [dispatch/push-to-players! (stub :push-to-players!)])

    (it "fails if connection-id is not host"
      (let [non-host-player @patches
            response (sut/ws-start-game {:connection-id (:conn-id non-host-player)})]
        (should= :fail (:status response))
        (should-be-nil (:payload response))
        (should= "Only the host can start the game!" (apic/flash-text response 0))))

    (it "succeeds if connection-id is host"
      (let [host-player @lautrec
            response (sut/ws-start-game {:connection-id (:conn-id host-player)})]
        (should= :ok (:status response))
        (should= (assoc @ds/firelink :state :started) (:payload response))))

    (it "starts game"
      (let [response (sut/ws-start-game {:connection-id (:conn-id @lautrec)})]
        (should= :ok (:status response))
        (should= :started (:state (:payload response)))))

    (it "adds letter"
      (let [response (sut/ws-start-game {:connection-id (:conn-id @lautrec)})]
        (should= :ok (:status response))
        (should-contain (:letter (:payload response)) roomc/letters)))

    (it "includes categories"
      (with-redefs [shuffle               (stub :shuffle {:invoke reverse})
                    categories/categories (map str (range 0 100))]
        (let [response (sut/ws-start-game {:connection-id (:conn-id @lautrec)})]
          (should= (map str (reverse (range 88 100))) (:categories (:payload response))))))

    (it "includes round start time"
      (with-redefs [time/now (constantly (time/now))]
        (let [response (sut/ws-start-game {:connection-id (:conn-id @lautrec)})]
          (should= :ok (:status response))
          (should= (time/now) (:round-start (:payload response))))))

    (it "notifies players of game start"
      (let [response (sut/ws-start-game {:connection-id (:conn-id @lautrec)})]
        (should= :ok (:status response))
        (should-have-invoked :push-to-players! {:with [(map db/entity (:players @firelink))
                                                       :room/update
                                                       [@firelink]]}))))

  (context "ws-submit-answers"

    (it "fails if payload not a map"
      (let [response (sut/ws-submit-answers {:payload       :blah
                                             :connection-id (:conn-id @patches)})]
        (should= :fail (:status response))
        (should= "Answer payload must be a map!" (apic/flash-text response 0))))

    (it "fails if player not found"
      (let [response (sut/ws-submit-answers {:payload       {}
                                             :connection-id (:conn-id :not-an-id)})]
        (should= :fail (:status response))
        (should= "Player not found!" (apic/flash-text response 0))))

    (it "adds answers to player"
      (let [answers  {"category1" "answer1"
                      "category2" "answer2"
                      "category3" "answer3"}
            response (sut/ws-submit-answers {:payload       answers
                                             :connection-id (:conn-id @patches)})]
        (should= :ok (:status response))
        (should= nil (:payload response))
        (should= (map answerc/->answer answers) (:answers @patches))))))
