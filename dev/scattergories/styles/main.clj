(ns scattergories.styles.main
  (:refer-clojure :exclude [rem])
  (:require [garden.def :as garden]
            [scattergories.styles.core :as core]
            [scattergories.styles.components.menus :as menus]
            [scattergories.styles.elements.forms :as forms]
            [scattergories.styles.elements.lists :as lists]
            [scattergories.styles.elements.media :as media]
            [scattergories.styles.elements.tables :as tables]
            [scattergories.styles.elements.typography :as typography]
            [scattergories.styles.layout.document :as document]
            [scattergories.styles.layout.mini-classes :as mini-classes]
            [scattergories.styles.layout.page :as page]
            [scattergories.styles.layout.reset :as reset]
            [scattergories.styles.layout.structure :as structure]
            [scattergories.styles.media.responsive :as responsive]
            [scattergories.styles.pages.authentication :as authentication]
            [scattergories.styles.pages.authentication :as authentication]
            ))

(garden/defstyles screen

; Layout
reset/screen
document/screen
page/screen
structure/screen
mini-classes/screen

; Elements
typography/screen
forms/screen
lists/screen
media/screen
tables/screen

; Componenents
menus/screen

; Pages
authentication/screen

; Media
responsive/screen

; Fonts
core/fonts

)
