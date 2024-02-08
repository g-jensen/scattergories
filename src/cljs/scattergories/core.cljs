(ns scattergories.core
  (:require [accountant.core :as accountant]
            [c3kit.wire.js :as wjs]
            [secretary.core :as secretary])
  (:import (goog History)))

(defn goto! [path]
  (when path
    (if (secretary/locate-route path)
      (accountant/navigate! path)
      (wjs/redirect! path))))

(defn go-home! [] (goto! "/"))
