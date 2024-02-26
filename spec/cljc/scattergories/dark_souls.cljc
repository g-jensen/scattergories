(ns scattergories.dark-souls
  (:require [c3kit.bucket.api :as db]
            [c3kit.bucket.spec-helperc :as helperc]
            [scattergories.roomc :as roomc]
            [scattergories.schema.room :as room.schema]
            [scattergories.schema.player :as player.schema]
            [speclj.core #?(:clj :refer :cljs :refer-macros) [before]])
  #?(:clj (:import (clojure.lang IDeref))))

(def schemas [room.schema/all
              player.schema/all])

(def firelink-atom (atom nil))

(deftype Entity [atm]
  #?(:clj IDeref :cljs cljs.core/IDeref)
  (#?(:clj deref :cljs -deref) [this] (db/reload @atm)))

(def firelink (Entity. firelink-atom))                      ;; the room

(defn init []
  (reset! firelink-atom (roomc/->room "dacode")))

(defn with-schemas
  ([] (with-schemas schemas))
  ([& schemas] (helperc/with-schemas schemas)))

(defn init-with-schemas []
  (list (with-schemas)
    (before (init))))