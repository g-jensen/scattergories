(ns scattergories.routes-spec
  (:require-macros [scattergories.spec-helperc :refer [it-routes]]
                   [speclj.core :refer [around before context describe it should= stub with-stubs]])
  (:require [scattergories.page :as page]
            [scattergories.routes :as sut]
            [secretary.core :as secretary]
            [speclj.core]))

(describe "Routes"
  (with-stubs)
  (before (page/clear!)
    (secretary/reset-routes!)
    (sut/app-routes))

  (around [it] (with-redefs [sut/load-page! (stub :load-page!)] (it)))

  (it-routes "/" :home)
  (it-routes "/room/shrine" :room
             (should= "shrine" (:room-code @page/state)))
  (it-routes "/room/depths" :room
             (should= "depths" (:room-code @page/state)))
  )