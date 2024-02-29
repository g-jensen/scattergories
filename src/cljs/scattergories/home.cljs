(ns scattergories.home
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.bucket.api :as db]
            [c3kit.wire.websocket :as ws]
            [scattergories.core :as cc]
            [scattergories.layoutc :as layoutc]
            [scattergories.page :as page]
            [scattergories.state :as state]))

(defn home [_]
  (fn [room]
    [:div.homepage-container
     [:h1 "Welcome to Scattergories"]
     [:div.nickname-input
      [:input {:type "text" :id "nickname" :placeholder "Enter your nickname"}]]
     [:div.room-actions
      [:p "Your room, sir: " @room]
      [:button {:id       "createRoom"
                :on-click #(ws/call! :room/create {:nickname "bob"}
                                     (fn [room]
                                       (db/tx* room)
                                       (prn "@db/impl: " @db/impl)  ))}
       "Create Room"]]]))

(defmethod page/render :home [_]
  [home state/room])