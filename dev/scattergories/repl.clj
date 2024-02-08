(ns scattergories.repl
  (:require
   [scattergories.init :as init]
   [scattergories.main :as main]))

(println "Welcome to the Scattergories REPL!")
(println "Initializing")
(init/install-legend!)
(main/start-db)
(require '[c3kit.bucket.api :as db])
