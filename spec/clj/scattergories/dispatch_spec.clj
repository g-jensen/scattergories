(ns scattergories.dispatch-spec
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.wire.websocket :as ws]
            [scattergories.dark-souls :as ds :refer [firelink lautrec frampt patches]]
            [scattergories.dispatch :as sut]
            [speclj.core :refer :all]))

(describe "Dispatch"
  (with-stubs)
  (ds/init-with-schemas)

  (context "pushing"
    (redefs-around [ws/connected-ids (fn [] (set (map :conn-id [@lautrec @frampt @patches])))
                    ws/push! (stub :ws/push!)])

    (context "to member"
      (it "no connections"
        (with-redefs [ws/connected-ids (fn [] [])]
          @(sut/push-to-player! [@lautrec] :some/method [@firelink])
          (should-not-have-invoked :ws/push!)))

      (it "no members"
        @(sut/push-to-player! [] :some/method [@firelink])
        (should-not-have-invoked :ws/push!))

      (it "one member and many connections"
        @(sut/push-to-player! @lautrec :some/method [@firelink])
        (should-have-invoked :ws/push! {:times 1})
        (should-have-invoked :ws/push! {:with ["conn-lautrec" :some/method [@firelink]]})))

    (context "to members"

      (it "no connections"
        (with-redefs [ws/connected-ids (fn [] [])]
          @(sut/push-to-players! [@lautrec] :some/method [@firelink])
          (should-not-have-invoked :ws/push!)))

      (it "no members"
        @(sut/push-to-players! [] :some/method [@firelink])
        (should-not-have-invoked :ws/push!))

      (it "one member and many connections"
        @(sut/push-to-players! [@lautrec] :some/method [@firelink])
        (should-have-invoked :ws/push! {:times 1})
        (should-have-invoked :ws/push! {:with ["conn-lautrec" :some/method [@firelink]]}))

      (it "two members and many connections"
        @(sut/push-to-players! [@lautrec @frampt] :some/method [@firelink])
        (should-have-invoked :ws/push! {:times 2})
        (should-have-invoked :ws/push! {:with ["conn-lautrec" :some/method [@firelink]]})
        (should-have-invoked :ws/push! {:with ["conn-frampt" :some/method [@firelink]]})))))