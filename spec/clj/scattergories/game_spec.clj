(ns scattergories.game-spec
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.apron.time :as time]
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

  (context "run-round!"
    (redefs-around [sut/sleep!                (stub :sleep!)
                    dispatch/push-to-players! (stub :push-to-players!)])
    (tags :slow)

    (it "waits for 3 minutes"
      (with-redefs [sut/all-submitted? (constantly true)]
        (sut/-run-round @firelink sut/timeout)
        (should-have-invoked :sleep! {:with [180000]})))

    (it "waits for answers to come in and then dispatches"
      (playerc/add-answers! @lautrec {"category1" "lautrec answer"})
      (playerc/add-answers! @frampt {"category1" "frampt answer"})
      (future (Thread/sleep 300)
              (playerc/add-answers! @patches {"category1" "patches answer"}))
      (sut/-run-round @firelink sut/timeout)
      (let [answers (roomc/find-answers @firelink)
            players (map db/entity (:players @firelink))]
        (should-have-invoked :push-to-players! {:with [(map db/entity (:players @firelink))
                                                       :room/update
                                                       (cons (assoc @firelink :state :reviewing
                                                                              :category-idx 0)
                                                             (concat players answers))]})
        (should= :reviewing (:state @firelink))))

    (it "does not wait for longer than timeout"
      (sut/-run-round @firelink 300)
      (should 1)))

  (context "ws-start-game"
    (redefs-around [sut/run-round!            (stub :run-round!)
                    dispatch/push-to-players! (stub :push-to-players!)])

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
                                                       [@firelink]]})))

    (it "runs round"
      (sut/ws-start-game {:connection-id (:conn-id @lautrec)})
      (should-have-invoked :run-round! {:with [@firelink 15000]})))

  (context "ws-submit-answers"

    (it "fails if payload not a map"
      (let [response (sut/ws-submit-answers {:params       :blah
                                             :connection-id (:conn-id @patches)})]
        (should= :fail (:status response))
        (should= "Answer payload must be a map!" (apic/flash-text response 0))))

    (it "fails if player not found"
      (let [response (sut/ws-submit-answers {:params       {}
                                             :connection-id (:conn-id :not-an-id)})]
        (should= :fail (:status response))
        (should= "Player not found!" (apic/flash-text response 0))))

    (it "adds answers to player"
      (let [answers  {"category1" "answer1"
                      "category2" "answer2"
                      "category3" "answer3"}
            response (sut/ws-submit-answers {:params       answers
                                             :connection-id (:conn-id @patches)})]
        (should= :ok (:status response))
        (should= nil (:payload response))
        (let [answers (db/find :answer)]
          (should= (map :id answers) (:answers @patches))))))

  (context "ws-update-answer"
    (redefs-around [dispatch/push-to-players! (stub :push-to-players!)])

    (it "fails if answer not found"
      (let [response (sut/ws-update-answer {:params       {:answer-id :not-an-id
                                                            :state     :bonus}
                                            :connection-id (:conn-id @lautrec)})]
        (should= :fail (:status response))
        (should= "Answer not found!" (apic/flash-text response 0))))

    (it "fails if state is invalid"
      (let [response (sut/ws-update-answer {:params       {:answer-id :not-an-id
                                                            :state     :blah}
                                            :connection-id (:conn-id @lautrec)})]
        (should= :fail (:status response))
        (should= "Invalid answer state!" (apic/flash-text response 0))))

    (it "saves new answer state"
      (playerc/add-answers! @lautrec {"category1" "lautrec answer"})
      (let [answer   (first (answerc/by-player @lautrec))
            response (sut/ws-update-answer {:params       {:answer-id (:id answer)
                                                            :state     :bonus}
                                            :connection-id (:conn-id @lautrec)})]
        (should= :ok (:status response))
        (should= :bonus (:state (first (answerc/by-player @lautrec))))))

    (it "dispatches new answer state to players"
      (playerc/add-answers! @lautrec {"category1" "lautrec answer"})
      (let [answer   (first (answerc/by-player @lautrec))]
        (sut/ws-update-answer {:params       {:answer-id (:id answer)
                                               :state     :bonus}
                               :connection-id (:conn-id @lautrec)})
        (should-have-invoked :push-to-players! {:with [(map db/entity (:players @firelink))
                                                       :room/update
                                                       [(first (answerc/by-player @lautrec))]]}))))

  (context "ws-next-category"
    (redefs-around [dispatch/push-to-players! (stub :push-to-players!)])

    (it "increments category index"
      (db/tx (roomc/next-category-idx (assoc @firelink :state :reviewing
                                                       :categories (mapv str (range 0 10)))))
      (let [response (sut/ws-next-category {:connection-id (:conn-id @lautrec)})]
        (should= 1 (:category-idx (:payload response)))))

    (it "dispatches room state and players"
      (db/tx (roomc/next-category-idx (assoc @firelink :state :reviewing
                                                       :categories (mapv str (range 0 10)))))
      (sut/ws-next-category {:connection-id (:conn-id @lautrec)})
      (should-have-invoked :push-to-players! {:with [(map db/entity (:players @firelink))
                                                     :room/update
                                                     (cons @firelink (map db/entity (:players @firelink)))]}))

    (it "awards points according to state"
      (db/tx (roomc/next-category-idx (assoc @firelink :state :reviewing
                                                       :categories (mapv str (range 0 10)))))
      (playerc/add-answers! @lautrec {"0" "lautrec answer 1"
                                      "1" "lautrec answer 2"})
      (playerc/add-answers! @frampt {"0" "frampt answer"})
      (let [lautrec-answer (db/entity (first (:answers @lautrec)))
            frampt-answer   (db/entity (first (:answers @frampt)))]
        (db/tx (assoc lautrec-answer :state :bonus))
        (db/tx (assoc frampt-answer :state :declined))
        (sut/ws-next-category {:connection-id (:conn-id @lautrec)}))
      (should= 2 (:points @lautrec))
      (should= 0 (:points @frampt))
      (should= 0 (:points @patches)))

    (it "resets answers and room if last category index"
      (db/tx (roomc/next-category-idx (assoc @firelink :state :reviewing
                                                       :categories (mapv str (range 0 1)))))
      (playerc/add-answers! @lautrec {"0" "lautrec answer 1"})
      (playerc/add-answers! @frampt {"0" "frampt answer"})
      (sut/ws-next-category {:connection-id (:conn-id @lautrec)})
      (should= 1 (:points @lautrec))
      (should= 1 (:points @frampt))
      (should= 0 (:points @patches))
      (should= :lobby (:state @firelink))
      (should (empty? (roomc/find-answers @firelink)))
      (should= [] (db/find :answer)))
    
    (it "dispatches reset answers to room if last category index" 
          (db/tx (roomc/next-category-idx (assoc @firelink :state :reviewing
                                                 :categories (mapv str (range 0 1)))))
          (playerc/add-answers! @lautrec {"0" "lautrec answer 1"})
          (playerc/add-answers! @frampt {"0" "frampt answer"})
          (let [answers (mapv :id (roomc/find-answers @firelink))
                deleted (map (fn [id] {:kind :answer :id id :db/delete? true}) answers)] 
            (prn "test deleted" deleted)
            (sut/ws-next-category {:connection-id (:conn-id @lautrec)})
            (should-have-invoked :push-to-players! {:with [(map db/entity (:players @firelink))
                                                           :room/update
                                                           deleted]})))))
