(ns scattergories.dispatch-spec
  (:require [scattergories.dark-souls :as ds]
            [scattergories.dispatch :as sut]
            [speclj.core :refer :all]))

(describe "Dispatch"
  (with-stubs)
  (ds/init-with-schemas)

  (context "push-to-players"
    ))