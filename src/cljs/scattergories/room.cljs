(ns scattergories.room
  (:require [scattergories.core :as cc]
            [scattergories.layoutc :as layoutc]
            [scattergories.page :as page]))

(defmethod page/render :room [_]
  [:div.main-container
   [:div.left-container
    [:br]
    [:br]
    [:h3 "Players"]
    [:ul
     [:li "Fleg Griffin II"]
     [:li "Peta"]]]
   [:div.center
    [:div.game-container
     [:h1 "Scattergories"]
     [:div.letter-display
      [:h2.categories-data "Letter: " [:span#letter "A"]]]
     [:div.timer
      [:h2.categories-data "Time Left: " [:span#time "180"] " seconds"]]
     [:div.categories
      [:p "Color:"]
      [:input {:type "text" :id "Color" :name "Color"}]
      [:p "Animal:"]
      [:input {:type "text" :id "Animal" :name "Animal"}]
      [:p "Food:"]
      [:input {:type "text" :id "Food" :name "Food"}]
      [:p "Really:"]
      [:input {:type "text" :id "Really long name" :name "Really long name"}]]]]
   [:div
    [:br]]])