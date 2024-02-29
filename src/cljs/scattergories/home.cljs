(ns scattergories.home
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.bucket.api :as db]
            [c3kit.wire.js :as wjs]
            [c3kit.wire.websocket :as ws]
            [reagent.core :as reagent]
            [scattergories.page :as page]))

(defn join-room! [[code]]
  (wjs/redirect! (str "/room/" code)))

(defn- create-room! [nickname]
  (when nickname
    (ws/call! :room/create {:nickname nickname} join-room!)))

(defn home []
  (let [nickname (reagent/atom nil)]
    (fn []
      [:div.homepage-container
       [:h1 "Welcome to Scattergories"]
       [:div.nickname-input
        [:input {:type "text"
                 :id "-nickname-input"
                 :placeholder "Enter your nickname"
                 :value @nickname
                 :on-change #(reset! nickname (wjs/e-text %))}]]
       [:div.room-actions
        [:button {:id       "-create-room-button"
                  :on-click #(create-room! @nickname)}
         "Create Room"]]])))

(defmethod page/render :home [_]
  [home])