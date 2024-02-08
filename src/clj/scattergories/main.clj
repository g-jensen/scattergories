(ns scattergories.main
  (:require [scattergories.config :as config]
            [scattergories.init :as init]
            [c3kit.apron.app :as app]
            [c3kit.apron.log :as log]
            [c3kit.apron.util :as util]
            [c3kit.bucket.bg :as bg]
            [c3kit.bucket.api :as db]
            [c3kit.wire.websocket :as websocket])
  (:import (java.lang Runtime Thread)))

(defn start-env [app] (app/start-env app "cc.env" "CC_ENV"))

(def env (app/service 'scattergories.main/start-env 'c3kit.apron.app/stop-env))
(def http (app/service 'scattergories.http/start 'scattergories.http/stop))

(def all-services [env db/service http websocket/service bg/service #_bg-tasks])
(def refresh-services [db/service bg/service])

(defn maybe-init-dev []
  (when config/development?
    (let [refresh-init (util/resolve-var 'c3kit.apron.refresh/init)]
      (refresh-init refresh-services "scattergories" ['scattergories.http 'scattergories.main]))))

(def start-db #(app/start! [db/service]))
(def start-all #(app/start! all-services))
(def stop-all #(app/stop! all-services))

(defn -main []
  (log/report "----- STARTING Scattergories SERVER -----")
  (log/report "scattergories environment: " config/environment)
  (log/set-level! (config/env :log-level :warn))
  (init/install-legend!)
  (init/configure-api!)
  (maybe-init-dev)
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-all))
  (.addShutdownHook (Runtime/getRuntime) (Thread. shutdown-agents))
  (start-all))
