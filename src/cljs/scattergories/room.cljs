(ns scattergories.room
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.wire.js :as wjs]
            [c3kit.wire.websocket :as ws]
            [clojure.string :as str]
            [reagent.core :as reagent]
            [scattergories.core :as cc]
            [scattergories.layoutc :as layoutc]
            [c3kit.bucket.api :as db]
            [scattergories.page :as page]
            [scattergories.playerc :as playerc]
            [scattergories.state :as state]))

(defn- join-room! []
  (when (not (str/blank? @state/nickname))
    (ws/call! :room/join
              {:nickname @state/nickname :room-code (:room-code @page/state)}
              db/tx*)))

(defn nickname-prompt [_]
  (let [local-nickname-ratom (reagent/atom nil)]
    (fn [nickname-ratom]
      [:div.center-div.margin-top-plus-5
       {:id "-nickname-prompt"}
       [:h1 "Enter nickname to join room..."]
       [:div.center
        [:input {:type "text"
                 :id "-nickname-input"
                 :placeholder "Enter your nickname"
                 :value @local-nickname-ratom
                 :on-change #(reset! local-nickname-ratom (wjs/e-text %))}]
        [:button {:id "-join-button"
                  :on-click #(do (reset! nickname-ratom @local-nickname-ratom)
                                 (join-room!))}
         "Join"]]])))

(defn get-me []
  (when ws/client
    (playerc/by-conn-id (:id (:connection @ws/client)))))

(defn- host? [room player]
  (= (:host room) (:id player)))

(defn waiting [room-ratom]
  [:<>
   [:h2.center.categories-data "Waiting for host to start game..."]
   [:h3.center "How to play"]
   [:p.text-align-center "When the host starts the game, you will be given a letter of the alphabet and a list of categories. The goal of the game is to find words in each category that start with the given letter."]
   [:p.text-align-center "For example, if the letter is \"C\" and a category is \"Types of Fish,\" an answer for that category could be \"Carp\" and you would be awarded a point."]
   [:p.text-align-center "At the end of a round, everyone's answer for each category will be shown. The host will then remove duplicate answers, awarding no points to players with that answer."]
   (if (host? @room-ratom (get-me))
     [:div.center
      [:button {:id "-start-button"
                :on-click #(ws/call! :game/start {} db/tx)} "Start Game"]])])

(defn playing [room-ratom]
  [:<>
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
    [:input {:type "text" :id "Really long name" :name "Really long name"}]]])

(defn room [room-ratom players-ratom]
  [:div.main-container
   {:id "-room"}
   [:div.left-container
    [:br]
    [:br]
    [:h3 "Players"]
    [:ul
     [:<>
      (ccc/for-all [player @players-ratom]
        [:li {:key (:id player)
              :id  (str "-player-" (:id player))}
         (str (:nickname player) (when (host? @room-ratom player) " (Host)"))])]]]
   [:div.center
    [:div.game-container
     [:h1 "Scattergories"]
     (if-not (= :started (:state @room-ratom))
       [waiting room-ratom]
       [playing room-ratom])]]])

(defn nickname-prompt-or-room [nickname-ratom]
  [:div {:id "-prompt-or-room"}
   (if (str/blank? @nickname-ratom)
     [nickname-prompt nickname-ratom]
     [room state/room state/players])])

(defn- fetch-room []
  (ws/call! :room/fetch
            {:room-code (:room-code @page/state)}
            db/tx*))

(defn maybe-render-room [room-ratom]
  (if @room-ratom
    [nickname-prompt-or-room state/nickname]
    [:h1 {:id "-room-not-found"} "Room not found!"]))

(defmethod page/entering! :room [_]
  (fetch-room))

(defmethod page/render :room [_]
  [maybe-render-room state/room])