(ns scattergories.layouts
  (:require [scattergories.config :as config]
            [scattergories.layoutc :as layoutc]
            [c3kit.apron.legend :as legend]
            [c3kit.apron.utilc :as utilc]
            [c3kit.wire.api :as api]
            [c3kit.wire.assets :refer [add-fingerprint]]
            [c3kit.wire.flash :as flash]
            [c3kit.wire.jwt :as jwt]
            [clojure.string :as str]
            [hiccup.element :as elem]
            [hiccup.page :as page]
            [ring.util.response :as response]))

(def default-title "Scattergories")
(def default-description "The Scattergories App")
(def default-image "/images/logos/cc-emblem.png")
(defn title [options] (or (:title options) default-title))

(defn social-meta [options]
  (list
    [:meta {:property "og:url" :content (or (:og/url options) (str config/host "/"))}]
    [:meta {:property "og:title" :content (or (:og/title options) (title options))}]
    [:meta {:property "og:description" :content (or (:og/description options) default-description)}]
    [:meta {:property "og:image" :content (or (:og/image options) default-image)}]
    [:meta {:name "twitter:card" :content "summary"}]
    [:meta {:name "twitter:site" :content "@thecleancoders"}]))

(defn default
  ([body] (default body {}))
  ([body options]
   (-> (response/response
         (page/html5
           [:head
            [:title (title options)]
            [:meta {:charset "utf-8"}]
            [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0, minimum-scale=1.0"}]
            (social-meta options)
            [:link {:rel "icon" :sizes "16x16" :type "image/png" :href "/images/favicons/favicon-16x16.png"}]
            [:link {:rel "icon" :sizes "32x32" :type "image/png" :href "/images/favicons/favicon-32x32.png"}]
            [:link {:rel "icon" :sizes "192x192" :type "image/png" :href "/images/favicons/favicon-192x192.png"}]
            [:link {:rel "icon" :sizes "512x512" :type "image/png" :href "/images/favicons/favicon-512x512.png"}]
            [:link {:rel "apple-touch-icon" :sizes "180x180" :href "/images/favicons/apple-touch-icon.png"}]
            [:link {:rel "manifest" :href "/images/favicons/site.webmanifest"}]
            [:script {:src "https://kit.fontawesome.com/982c21555a.js" :crossorigin "anonymous"}]
            (if config/development?
              (list
                (page/include-js "/cljs/goog/base.js")
                (page/include-js "/cljs/scattergories_dev.js"))
              (page/include-js (add-fingerprint "/cljs/scattergories.js")))
            (:head options)                                 ;; MDM - must go after js so we can include js-fns, and before css, so we can override styles as needed
            (page/include-css (add-fingerprint (or (:css options) "/css/scattergories.css")))]
           [:body body]))

       (response/content-type "text/html")
       (response/charset "UTF-8"))))

(defn static [& content]
  (default
    [:div#content
     content]))

(defn not-found []
  (static
    [:div.center-div.margin-top-plus-2
     [:h2.margin-bottom-0 "Amidst ancient stones and echoes past, the path you seek has faded fast."]
     [:h2.margin-bottom-0.text-align-center "Page not found (404)"]
     [:img.center-div {:src "/images/not-found.webp" :style "max-width: 60%;"}]]))

(defn client-init
  ([] (client-init {}))
  ([data]
   (let [payload (pr-str (utilc/->transit data))]
     (str "<script type=\"text/javascript\">\n//<![CDATA[\n"
          "scattergories.main.main(" (str/replace payload "</script>" "<\\/script>") ");"
          "\n//]]>\n</script>"))))

(def rich-client-placeholder (static "Your page is loading..."))

(defn rich-client [payload options]
  (default (list [:div#app-root rich-client-placeholder]
                 (client-init payload))
           (assoc options
             :head (elem/javascript-tag (str "goog.require('scattergories.main');")))))

(defn build-rich-client-payload [request]
  {:flash  (flash/messages request)
   :config {
            :anti-forgery-token (jwt/client-id request)
            :ws-csrf-token      (jwt/client-id request)
            :api-version        (api/version)
            :environment        config/environment
            :google-client-id   (-> config/env :google-oauth :client-id)
            :scattergories-root          (-> config/env :cleancoders-auth :url-root)
            :host               config/host
            }})

(defn web-rich-client
  "Load the default web page and let the client side take over."
  ([request] (web-rich-client request {}))
  ([request options] (rich-client (build-rich-client-payload request) options)))

