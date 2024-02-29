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
    [:button {:id       "createRoom"
              :on-click #(ws/call! :room/create {:nickname "bob"}
                                   (fn [room]
                                     (prn "room: " room)
                                     (ws/call! :room/join {:nickname  "bob"
                                                           :room-code (:code room)}
                                               (fn [& args]
                                                 (prn "args: " args)))))}
     "Create Room"]]])