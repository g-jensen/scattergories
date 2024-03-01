(ns scattergories.answerc)

(defn ->answer [[category answer]]
  {:kind     :answer
   :category category
   :answer   answer})