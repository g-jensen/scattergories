(ns scattergories.http
  (:require [scattergories.config :as config]
            [scattergories.layouts :as layouts]
            [c3kit.apron.log :as log]
            [c3kit.apron.time :as time]
            [c3kit.apron.util :as util]
            [c3kit.wire.assets :refer [wrap-asset-fingerprint]]
            [c3kit.wire.jwt :as jwt]
            [c3kit.wire.jwt :refer [wrap-jwt]]
            [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.flash :refer [wrap-flash]]
            [ring.middleware.head :refer [wrap-head]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.nested-params :refer [wrap-nested-params]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]))

(defn refreshable [handler-sym]
  (if config/development?
    (fn [request] (@(util/resolve-var handler-sym) request))
    (util/resolve-var handler-sym)))

(defroutes web-handler
  (refreshable 'scattergories.routes/handler)
  (route/not-found (layouts/not-found)))

(defn app-handler []
  (if config/development?
    (let [wrap-verbose    (util/resolve-var 'c3kit.apron.verbose/wrap-verbose)
          refresh-handler (util/resolve-var 'c3kit.apron.refresh/refresh-handler)]
      (-> (refresh-handler 'scattergories.http/web-handler)
          wrap-verbose))
    (util/resolve-var 'scattergories.http/web-handler)))

;; MDM - What's all this refresh/development hocus pocus?  An explanation owed.
;;  In development, we want changed code to automatically reload when a request is made.  Although simple in
;;  principle, the mechanics of it give me a headache sometimes.
;;  1) When the app starts, some namespaces are loaded, like this one.  But the refresh code (scattergories.refresh)
;;      doesn't know which. As far as it knows, nothing has been loaded.  So on the first request, all the namespaces
;;      are reloaded.
;;  2) Some namespaces will/should never get reloaded. See scattergories.refresh/excludes
;;  3) The root-handler below is expensive to create.  Hence the defonce.  So we carefully pick pieces of the
;;      root-handler to refresh:
;;        - scattergories.routes/handler - the essence of scattergories.com
;;        - wrap-session - because it uses the database connection which gets reloaded

(defonce root-handler
  (-> (app-handler)
      wrap-flash
      (wrap-anti-forgery {:strategy (jwt/create-strategy)})
      (wrap-jwt {:cookie-name "scattergories-token" :secret (:jwt-secret config/env) :lifespan (when config/development? (time/hours 336))})
      wrap-keyword-params
      wrap-multipart-params
      wrap-nested-params
      wrap-params
      wrap-cookies
      (wrap-resource "public")
      wrap-asset-fingerprint
      wrap-content-type
      wrap-not-modified
      wrap-head
      ))

(defn start [app]
  (let [port (or (some-> "PORT" System/getenv Integer/parseInt) 8123)]
    (log/info (str "Starting HTTP server: http://localhost:" port))
    (let [server (run-server root-handler {:port port})]
      (assoc app :http server))))

(defn stop [app]
  (when-let [stop-server-fn (:http app)]
    (log/info "Stopping HTTP server")
    (stop-server-fn :timeout 1000))
  (dissoc app :http))


