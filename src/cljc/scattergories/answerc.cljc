(ns scattergories.answerc
  (:require [c3kit.bucket.api :as db]))

(defn ->answer [[category answer]]
  {:kind     :answer
   :category category
   :answer   answer})

(defn create-answer! [category-pair]
  (db/tx (->answer category-pair)))