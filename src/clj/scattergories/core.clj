(ns scattergories.core
  (:require [c3kit.apron.schema :as schema]
            [c3kit.bucket.api :as db]))

(defn ensure-kinds-match! [kind existing]
  (when (and kind existing)
    (let [existing-kind (:kind existing)]
      (when-not (= kind existing-kind)
        (throw (Exception. (str "Merge entity kind mismatch. Got " existing-kind ", expected " kind ".")))))))

(defn merged-entity
  ([entity]
   (let [existing (db/entity (:id entity))]
     (ensure-kinds-match! (:kind entity) existing)
     (merge existing entity)))
  ([request kind]
   (let [entity-id (-> request :params :id)
         existing  (when entity-id (db/entity entity-id))]
     (ensure-kinds-match! kind existing)
     (merge existing (:params request)))))

(defn merged-conformed-entity
  ([entity schema]
   (let [entity (merged-entity entity)]
     (schema/conform schema entity)))
  ([request kind schema]
   (let [entity (merged-entity request kind)]
     (schema/conform schema entity))))
