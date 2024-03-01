(ns scattergories.room-spec
  (:require-macros [speclj.core :refer [redefs-around around stub should-have-invoked should-not-have-invoked with-stubs describe context it should= should-be-nil should-contain should should-not before should-not-be-nil]]
                   [c3kit.wire.spec-helperc :refer [should-not-select should-select]])
  (:require [accountant.core :as accountant]
            [c3kit.wire.js :as wjs]
            [reagent.core :as reagent]
            [scattergories.dark-souls :as ds]
            [scattergories.init :as init]
            [scattergories.page :as page]
            [scattergories.room :as sut]
            [c3kit.wire.spec-helper :as wire]
            [c3kit.bucket.api :as db]
            [scattergories.state :as state]
            [c3kit.wire.websocket :as ws]))

(def players-ratom (reagent/atom []))
(def room-ratom (reagent/atom nil))

(describe "Room"
  (init/install-reagent-db-atom!)
  (init/install-legend!)
  (init/configure-api!)
  (with-stubs)
  (wire/stub-ws)
  (wire/with-root-dom)
  (ds/init-with-schemas)
  (before (db/set-safety! false)
          (db/clear)
          (reset! state/nickname nil)
          (reset! players-ratom [])
          (reset! room-ratom nil))


  (it "fetches room on enter"
    (page/install-room! "A8SBLK")
    (should-be-nil @state/room)
    (page/entering! :room)
    (should-have-invoked :ws/call! {:with [:room/fetch {:room-code "A8SBLK"} db/tx*]}))

  (context "maybe render room"
    (before (wire/render [sut/maybe-render-room state/room state/nickname]))

    (it "renders error if no room"
      (should-select "#-room-not-found")
      (should-not-select "#-prompt-or-room"))

    ; once it works, it will probably fail ^this test
    ; TODO - [GMJ] Figure out why db/tx doesn't work in specs
    #_(it "renders room if found"
      (prn "depths" @ds/depths-atom)
      (db/tx @ds/depths-atom)
      (flush)
      (should-not-select "#-room-not-found")
      (should-select "#-prompt-or-room")))

  (context "nickname prompt or room"
    (before (wire/render [sut/nickname-prompt-or-room state/nickname]))

    (it "renders nickname prompt if no nickname"
      (should-select "#-nickname-prompt")
      (should-not-select "#-room"))

    (it "renders room if nickname"
      (reset! state/nickname "Lautrec")
      (wire/flush)
      (should-not-select "#-nickname-prompt")
      (should-select "#-room")))

  (context "nickname prompt"
    (before (wire/render [sut/nickname-prompt state/nickname]))

    (it "updates input on change"
      (wire/change! "#-nickname-input" "Lautrec")
      (should= "Lautrec" (wire/value "#-nickname-input"))
      (wire/change! "#-nickname-input" "Patches")
      (should= "Patches" (wire/value "#-nickname-input")))

    (context "button click"

      (context "updates nickname ratom"
        (it "when nickname is Lautrec"
          (wire/change! "#-nickname-input" "Lautrec")
          (should= nil @state/nickname)
          (wire/click! "#-join-button")
          (should= "Lautrec" @state/nickname))

        (it "when nickname is Patches"
          (wire/change! "#-nickname-input" "Patches")
          (should= nil @state/nickname)
          (wire/click! "#-join-button")
          (should= "Patches" @state/nickname)))

      (it "joins room"
        (wire/change! "#-nickname-input" "Lautrec")
        (wire/click! "#-join-button")
        (should-have-invoked :ws/call! {:with [:room/join
                                               {:nickname "Lautrec" :room-code "A8SBLK"}
                                               db/tx*]}))

      (it "doesn't join room if blank nickname"
        (wire/change! "#-nickname-input" " ")
        (should= nil @state/nickname)
        (wire/click! "#-join-button")
        (should-not-have-invoked :ws/call!))))

  (context "room"
    (before (wire/render [sut/room room-ratom players-ratom]))

    (context "displays players"

      (it "with one player"
        (reset! players-ratom [@ds/frampt-atom])
        (reset! room-ratom {:host (:id @ds/frampt-atom)})
        (wire/flush)
        (should-select (str "#-player-" (:id @ds/frampt-atom)))
        (should= "Kingseeker Frampt (Host)" (wire/html (str "#-player-" (:id @ds/frampt-atom)))))

      (it "with multiple players"
        (reset! players-ratom [@ds/frampt-atom @ds/lautrec-atom])
        (reset! room-ratom {:host (:id @ds/frampt-atom)})
        (wire/flush)
        (should-select (str "#-player-" (:id @ds/frampt-atom)))
        (should-select (str "#-player-" (:id @ds/lautrec-atom)))
        (should= "Kingseeker Frampt (Host)" (wire/html (str "#-player-" (:id @ds/frampt-atom))))
        (should= "Lautrec" (wire/html (str "#-player-" (:id @ds/lautrec-atom))))))

    (context "start button"
      (redefs-around [sut/get-me (fn [] @ds/frampt-atom)])
      (before (reset! players-ratom [@ds/frampt-atom @ds/lautrec-atom]))

      (it "does display if user is host"
        (reset! room-ratom {:host (:id @ds/frampt-atom)})
        (wire/flush)
        (should-select "#-start-button"))

      (it "doesn't display if user is not host"
        (reset! room-ratom {:host (:id @ds/lautrec-atom)})
        (wire/flush)
        (should-not-select "#-start-button"))

      (it "starts game on click"
        (reset! room-ratom {:host (:id @ds/frampt-atom)})
        (wire/flush)
        (wire/click! "#-start-button")
        (should-have-invoked :ws/call! {:with [:game/start {} db/tx]})))))