(ns scattergories.home-spec
  (:require-macros [speclj.core :refer [around stub should-have-invoked should-not-have-invoked with-stubs describe context it should= should-be-nil should-contain should should-not before should-not-be-nil]]
                   [c3kit.wire.spec-helperc :refer [should-not-select should-select]])
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.wire.js :as wjs]
            [scattergories.dark-souls :as ds]
            [scattergories.home :as sut]
            [scattergories.state :as state]
            [c3kit.wire.spec-helper :as wire]))

(defn stub-redirect! []
  (around [it]
          (with-redefs [wjs/redirect! (stub :redirect!)]
            (it))))

(describe "Home"
  (with-stubs)
  (wire/stub-ws)
  (stub-redirect!)
  (wire/with-root-dom)
  (ds/with-schemas)

  (context "joins room with code"
    (it "UK2LLJ"
      (sut/join-room! ["UK2LLJ"])
      (should-have-invoked :redirect! {:with ["/room/UK2LLJ"]}))

    (it "MA5BX1"
      (sut/join-room! ["MA5BX1"])
      (should-have-invoked :redirect! {:with ["/room/MA5BX1"]})))

  (context "create room"
    (before (wire/render [sut/home]))

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