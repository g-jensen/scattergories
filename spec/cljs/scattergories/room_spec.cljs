(ns scattergories.room-spec
  (:require-macros [speclj.core :refer [redefs-around around stub should-have-invoked should-not-have-invoked with-stubs describe context it should= should-be-nil should-contain should should-not before should-not-be-nil]]
                   [c3kit.wire.spec-helperc :refer [should-not-select should-select]])
  (:require [accountant.core :as accountant]
            [c3kit.apron.time :as time]
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
(def room-ratom (reagent/atom {}))

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
          (reset! room-ratom {}))


  (it "fetches room on enter"
    (page/install-room! "A8SBLK")
    (should-be-nil @state/room)
    (page/entering! :room)
    (should-have-invoked :ws/call! {:with [:room/fetch {:room-code "A8SBLK"} db/tx*]}))

  (context "maybe render room"
    (before (wire/render [sut/maybe-render-room room-ratom state/nickname]))

    (it "renders error if no room"
      (reset! room-ratom nil)
      (wire/flush)
      (should-select "#-room-not-found")
      (should-not-select "#-prompt-or-room"))

    (it "renders error if trying to join when room already started"
      (with-redefs [sut/get-me (constantly nil)]
        (should-select "#-room-started")))

    (it "renders room"
      (with-redefs [sut/get-me (constantly @ds/frampt-atom)]
        (wire/render [sut/maybe-render-room room-ratom state/nickname])
        (should-not-select "#-room-started")
        (should-not-select "#-room-not-found")
        (should-select "#-prompt-or-room"))))

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
        (should= "Kingseeker Frampt (Host) | 0" (wire/html (str "#-player-" (:id @ds/frampt-atom)))))

      (it "with multiple players"
        (reset! players-ratom [@ds/frampt-atom @ds/lautrec-atom])
        (reset! room-ratom {:host (:id @ds/frampt-atom)})
        (wire/flush)
        (should= "Kingseeker Frampt (Host) | 0" (wire/html (str "#-player-" (:id @ds/frampt-atom))))
        (should= "Lautrec | 0" (wire/html (str "#-player-" (:id @ds/lautrec-atom))))))

    (context "waiting"
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
          (should-have-invoked :ws/call! {:with [:game/start {} db/tx]}))))

    (context "playing"
      (redefs-around [time/now (constantly (time/now))])
      (before (swap! room-ratom assoc
                     :state :started
                     :letter nil
                     :round-start (time/now)
                     :categories []))

      (it "does render categories if time left"
        (wire/flush)
        (should-not-select "#-submitting")
        (should-select "#-categories"))

      (it "doesn't render categories if time is up"
        (swap! room-ratom assoc :round-start (js/Date. 0))
        (wire/flush)
        (should-select "#-submitting")
        (should-not-select "#-categories"))

      (it "renders letter"
        (swap! room-ratom assoc :letter "K")
        (wire/flush)
        (should= "K" (wire/html "#-letter"))
        (swap! room-ratom assoc :letter "M")
        (wire/flush)
        (should= "M" (wire/html "#-letter")))

      (it "renders time left"
        (with-redefs [wjs/interval (stub :interval)]
          (swap! room-ratom assoc :round-start (time/now))
          (wire/render [sut/room room-ratom players-ratom])
          (wire/flush)
          (should-have-invoked :interval)
          (should= "3:00" (wire/html "#time"))))

      (it "renders categories"
        (swap! room-ratom assoc :categories ["dogs" "cats" "others"])
        (wire/flush)
        (should-select "#-dogs")
        (should-select "#-cats")
        (should-select "#-others")))))