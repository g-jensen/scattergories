(ns scattergories.home
  (:require [accountant.core :as accountant]
            [c3kit.wire.js :as wjs]
            [c3kit.wire.websocket :as ws]
            [scattergories.state :as state]
            [scattergories.page :as page]))

(defn join-room! [[code]]
  (accountant/navigate! (str "/room/" code)))

(defn- create-room! [nickname]
  (when (not (empty? nickname))
    (ws/call! :room/create {:nickname nickname} join-room!)))

(defn home [nickname-ratom]
  [:div.homepage-container
   [:h1 "Welcome to Scattergories"]
   [:div.nickname-input
    [:input {:type "text"
             :id "-nickname-input"
             :placeholder "Enter your nickname"
             :value @nickname-ratom
             :on-change #(reset! nickname-ratom (wjs/e-text %))}]]
   [:div.room-actions
    [:button {:id       "-create-room-button"
              :on-click #(create-room! @nickname-ratom)}
     "Create Room"]]])

(defmethod page/render :home [_]
  [home state/nickname])