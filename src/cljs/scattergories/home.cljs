(ns scattergories.home
  (:require [scattergories.core :as cc]
            [scattergories.layoutc :as layoutc]
            [scattergories.page :as page]))


(defmethod page/render :home [_]
  [:div.homepage-container
   [:h1 "Welcome to Scattergories"]
   [:div.nickname-input
    [:input {:type "text" :id "nickname" :placeholder "Enter your nickname"}]]
   [:div.room-actions
    [:input {:type "text" :id "roomCode" :placeholder "Room Code"}]]
   [:div.room-actions
    [:button {:id "joinRoom"} "Join Room"]
    [:button {:id "createRoom"} "Create Room"]]])