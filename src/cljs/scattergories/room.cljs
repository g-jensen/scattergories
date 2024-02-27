(ns scattergories.room
  (:require [scattergories.core :as cc]
            [scattergories.layoutc :as layoutc]
            [scattergories.page :as page]))

(defmethod page/render :room [_]
  [:h1 (str "hello!" @page/state)])