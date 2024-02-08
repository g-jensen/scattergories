(ns scattergories.cleandevdb
  (:require [datomic.api :as datomic]))

(def dev-uri "datomic:dev://localhost:4334/scattergories")

(defn -main []
  (println "Explicitly using dev datomic URI: " dev-uri)
  (println "\t deleting database")
  (datomic/delete-database dev-uri)
  (println "\t creating database")
  (datomic/create-database dev-uri)
  (println "\t done!")
  (System/exit 0))
