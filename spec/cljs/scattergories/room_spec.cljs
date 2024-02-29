(ns scattergories.room-spec
  (:require-macros [speclj.core :refer [around stub should-have-invoked should-not-have-invoked with-stubs describe context it should= should-be-nil should-contain should should-not before should-not-be-nil]]
                   [c3kit.wire.spec-helperc :refer [should-not-select should-select]])
  (:require [accountant.core :as accountant]
            [c3kit.wire.js :as wjs]
            [scattergories.dark-souls :as ds]
            [scattergories.room :as sut]
            [c3kit.wire.spec-helper :as wire]
            [scattergories.state :as state]))

(describe "Room"
  (with-stubs)
  (wire/stub-ws)
  (wire/with-root-dom)
  (ds/with-schemas)
  (before (reset! state/nickname nil))


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

    (context "updates nickname ratom on button click"
      (it "when nickname is Lautrec"
        (wire/change! "#-nickname-input" "Lautrec")
        (should= nil @state/nickname)
        (wire/click! "#-join-button")
        (should= "Lautrec" @state/nickname))

      (it "when nickname is Patches"
        (wire/change! "#-nickname-input" "Patches")
        (should= nil @state/nickname)
        (wire/click! "#-join-button")
        (should= "Patches" @state/nickname)))))