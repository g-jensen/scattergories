(ns scattergories.playerc)

(defn ->player [nickname]
  {:kind     :player
   :nickname nickname})