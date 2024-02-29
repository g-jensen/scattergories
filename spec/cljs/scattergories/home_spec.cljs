(ns scattergories.home-spec
  (:require-macros [speclj.core :refer [around stub should-have-invoked should-not-have-invoked with-stubs describe context it should= should-be-nil should-contain should should-not before should-not-be-nil]])
  (:require [accountant.core :as accountant]
            [c3kit.wire.js :as wjs]
            [scattergories.dark-souls :as ds]
            [scattergories.home :as sut]
            [c3kit.wire.spec-helper :as wire]
            [scattergories.state :as state]))

(defn stub-navigate! []
  (around [it]
          (with-redefs [accountant/navigate! (stub :redirect!)]
            (it))))

(describe "Home"
  (with-stubs)
  (wire/stub-ws)
  (stub-navigate!)
  (wire/with-root-dom)
  (ds/with-schemas)
  (before (reset! state/nickname nil))

  (context "joins room with code"
    (it "UK2LLJ"
      (sut/join-room! ["UK2LLJ"])
      (should-have-invoked :redirect! {:with ["/room/UK2LLJ"]}))

    (it "MA5BX1"
      (sut/join-room! ["MA5BX1"])
      (should-have-invoked :redirect! {:with ["/room/MA5BX1"]})))

  (context "nickname input"
    (before (wire/render [sut/home state/nickname]))

    (it "updates value on change"
      (wire/change! "#-nickname-input" "Lautrec")
      (should= "Lautrec" @state/nickname)))

  (context "create room"
    (before (wire/render [sut/home state/nickname]))

    (it "does nothing if no nickname"
      (wire/click! "#-create-room-button")
      (should-not-have-invoked :ws/call!))

    (context "creates room if nickname"
      (it "is Lautrec"
        (wire/change! "#-nickname-input" "Lautrec")
        (wire/click! "#-create-room-button")
        (should-have-invoked :ws/call! {:with [:room/create {:nickname "Lautrec"} sut/join-room!]}))

      (it "is Patches"
        (wire/change! "#-nickname-input" "Patches")
        (wire/click! "#-create-room-button")
        (should-have-invoked :ws/call! {:with [:room/create {:nickname "Patches"} sut/join-room!]})))))