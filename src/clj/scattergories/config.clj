(ns scattergories.config
  (:require [c3kit.apron.app :as app]))

(def ^:private base
  {
   :analytics-code "console.log('google analytics would have loaded for this page');"
   :log-level      :info})

(def bucket {:impl        :memory
             :full-schema 'scattergories.schema.full/full-schema})

(def development
  (assoc base
    :database bucket
    :host "http://localhost:8123"
    :log-level :trace
    :jwt-secret "ACME_DEV_SECRET"))

(def staging
  (assoc base
    :database bucket
    :host "https://scattergories-staging.cleancoders.com"
    :log-level :trace
    :jwt-secret "ACME_STAGING_SECRET"))

(def production
  (assoc base
    :database bucket
    :host "https://scattergories.cleancoders.com"
    :analytics-code "console.log('Replace me with Real Google Analytics Code.');"
    :jwt-secret "ACME_PRODUCTION_SECRET"))

(def environment (app/find-env "cc.env" "CC_ENV"))
(def development? (= "development" environment))
(def production? (= "production" environment))

(def env
  (case environment
    "staging" staging
    "production" production
    development))

(def host (:host env))

(defn link [& parts] (apply str host parts))
