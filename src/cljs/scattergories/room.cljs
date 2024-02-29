(ns scattergories.room
  (:require [c3kit.wire.js :as wjs]
            [reagent.core :as reagent]
            [scattergories.core :as cc]
            [scattergories.layoutc :as layoutc]
            [scattergories.page :as page]
            [scattergories.state :as state]))

(defn nickname-prompt [_]
  (let [local-nickname-ratom (reagent/atom nil)]
    (fn [nickname-ratom]
      [:div.center-div.margin-top-plus-5
       {:id "-nickname-prompt"}
       [:h1 "Enter nickname to join room..."]
       [:input {:type "text"
                :id "-nickname-input"
                :placeholder "Enter your nickname"
                :value @local-nickname-ratom
                :on-change #(reset! local-nickname-ratom (wjs/e-text %))}]
       [:button {:id "-join-button"
                 :on-click #(reset! nickname-ratom @local-nickname-ratom)}
        "Join"]])))

(defn room []
  [:div.main-container
   {:id "-room"}
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
      [:input {:type "text" :id "Really long name" :name "Really long name"}]]]]])

(defn nickname-prompt-or-room [nickname-ratom]
  (if-not @nickname-ratom
    [nickname-prompt nickname-ratom]
    (do (room))))

(defmethod page/render :room [_]
  [nickname-prompt-or-room state/nickname])