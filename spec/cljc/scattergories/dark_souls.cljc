(ns scattergories.dark-souls
  (:require [c3kit.bucket.api :as db]
            [c3kit.bucket.spec-helperc :as helperc]
            [scattergories.playerc :as playerc]
            [scattergories.roomc :as roomc]
            [scattergories.schema.room :as room.schema]
            [scattergories.schema.player :as player.schema]
            [speclj.core #?(:clj :refer :cljs :refer-macros) [before]])
  #?(:clj (:import (clojure.lang IDeref))))

(def schemas [room.schema/all
              player.schema/all])

(def shrine-code "shrine")
(def depths-code "depths")

(def firelink-atom (atom nil))
(def lautrec-atom (atom nil))
(def frampt-atom (atom nil))
(def patches-atom (atom nil))
(def depths-atom (atom nil))

(deftype Entity [atm]
  #?(:clj IDeref :cljs cljs.core/IDeref)
  (#?(:clj deref :cljs -deref) [this] (db/reload @atm)))

(def firelink (Entity. firelink-atom))                      ;; a populated room
(def lautrec (Entity. lautrec-atom))                        ;; a player at firelink
(def frampt (Entity. frampt-atom))                          ;; a player at firelink
(def patches (Entity. patches-atom))                        ;; a player at firelink
(def depths (Entity. depths-atom))                          ;; an empty room

(defn init []
  (reset! firelink-atom (roomc/create-room! shrine-code))
  (reset! depths-atom (roomc/create-room! depths-code))
  (reset! lautrec-atom (db/tx (playerc/->player "Lautrec" "conn-lautrec")))
  (reset! frampt-atom (db/tx (playerc/->player "Kingseeker Frampt" "conn-frampt")))
  (reset! patches-atom (db/tx (playerc/->player "Patches" "conn-patches")))
  (db/tx (roomc/add-player @firelink @lautrec))
  (db/tx (roomc/add-player @firelink @frampt))
  (db/tx (roomc/add-player @firelink @patches)))

(defn with-schemas
  ([] (with-schemas schemas))
  ([& schemas] (helperc/with-schemas schemas)))

(defn init-with-schemas []
  (list (with-schemas)
    (before (init))))