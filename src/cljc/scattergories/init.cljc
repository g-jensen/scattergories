(ns scattergories.init
  (:require #?(:cljs [scattergories.core :as core])
            #?(:cljs [scattergories.page :as page])
            #?(:cljs [reagent.core :as reagent])
            [c3kit.apron.legend :as legend]
            [c3kit.bucket.api :as db]
            [c3kit.bucket.memory]
            [c3kit.wire.api :as api]
            [scattergories.config :as config]
            [scattergories.schema.full :as schema]
            ))

(defn install-legend! []
  (legend/init! {
                 :db/retract legend/retract
                 }))

#?(:cljs (defn install-reagent-db-atom! []
           (db/set-impl! (db/create-db config/bucket schema/full-schema))))

(defn configure-api! []
  (api/configure! #?(:clj  {:ws-handlers 'scattergories.routes/ws-handlers
                            :version     (api/version-from-js-file (if config/development? "public/cljs/scattergories_dev.js" "public/cljs/scattergories.js"))}
                     :cljs {:redirect-fn       core/goto!
                            })))
