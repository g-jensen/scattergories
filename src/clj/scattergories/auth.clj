(ns scattergories.auth
  (:require [c3kit.apron.corec :as ccc]
            [c3kit.apron.legend :as legend]
            [c3kit.bucket.api :as db]
            [c3kit.wire.ajax :as ajax]
            [c3kit.wire.apic :as apic]
            [c3kit.wire.flash :as flash]
            [c3kit.wire.jwt :as jwt]
            [c3kit.wire.websocket :as ws]
            [clojure.string :as str]
            [ring.util.response :as response])
  (:import (org.mindrot.jbcrypt BCrypt)))

(defn ajax-csrf-token [request]
  (let [{:keys [client-id]} (:jwt/payload request)]
    (-> {:ws-csrf-token      client-id
         :anti-forgery-token client-id}
      ajax/ok
      (jwt/copy-payload request))))

(defn check-password [password hash] (BCrypt/checkpw password hash))

(defn websocket-open [request]
  (ws/handler request {:read-csrf jwt/client-id}))