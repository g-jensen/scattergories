(ns scattergories.home
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.wire.websocket :as ws]
            [scattergories.core :as cc]
            [scattergories.layoutc :as layoutc]
            [scattergories.page :as page]))


(defmethod page/render :home [_]
  [:div.homepage-container
   [:h1 "Welcome to Scattergories"]
   [:div.nickname-input
    [:input {:type "text" :id "nickname" :placeholder "Enter your nickname"}]]
   [:div.room-actions
    [:button {:id "createRoom"
              :on-click #(ws/call! :room/create {:nickname "bob"} ccc/noop)}
     "Create Room"]]])