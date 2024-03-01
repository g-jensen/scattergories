(ns scattergories.categories
  (:require [clojure.string :as s]))

(def filename "categories.txt")
(def categories (s/split-lines (slurp filename)))