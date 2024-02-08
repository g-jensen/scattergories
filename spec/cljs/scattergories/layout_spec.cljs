(ns scattergories.layout-spec
  (:require-macros [c3kit.wire.spec-helperc :refer [should-not-select should-select]]
                   [speclj.core :refer [before describe it should-contain with-stubs]])
  (:require [scattergories.layout :as sut]
            [scattergories.page :as page]
            [c3kit.wire.ajax :as ajax]
            [c3kit.wire.flash :as flash]
            [c3kit.wire.spec-helper :as wire-helper]))

(defmethod page/render :layout/test [_] "Layout Test")

(describe "Layout"
  (with-stubs)
  (wire-helper/with-root-dom)
  (before (page/clear!)
          (flash/clear!)
          (page/install-page! :layout/test)
          (wire-helper/render [sut/default]))

  (it "structure"
    (should-select "#content")
    (should-contain "Layout Test" (wire-helper/html)))

  (it "flash"
    (should-not-select ".flash-root")
    (flash/add-success! "Yes!")
    (wire-helper/flush)
    (should-select ".flash-root"))

  (it "spinner"
    (should-not-select ".site-spinner")
    (swap! ajax/active-ajax-requests inc)
    (wire-helper/flush)
    (should-select ".site-spinner"))
  )
