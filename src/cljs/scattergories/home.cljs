(ns scattergories.home
  (:require [scattergories.core :as cc]
            [scattergories.layoutc :as layoutc]
            [scattergories.page :as page]))


(defmethod page/render :home [_]
  [:main
   [:section.home
    [:div.container.width-300.margin-top-plus-5.margin-bottom-plus-5
     ]]])