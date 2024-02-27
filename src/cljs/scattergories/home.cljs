(ns scattergories.home
  (:require [scattergories.core :as cc]
            [scattergories.layoutc :as layoutc]
            [scattergories.page :as page]))


(defmethod page/render :home [_]
  [:h1 "hi"])