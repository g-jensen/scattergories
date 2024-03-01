(ns scattergories.routes-spec
  (:require [scattergories.routes :as routes]
            [scattergories.spec-helper]
            [c3kit.wire.spec-helper :as wire-helper]
            [c3kit.wire.websocket :as ws]
            [scattergories.layouts]
            [speclj.core :refer :all]
            [speclj.stub :as stub]))

(def args (atom :none))

;; MDM - Macros are used here to preserve line number when specs fail

(defmacro check-route [path method handler]
  `(let [stub-key# ~(keyword handler)]
     (require '~(symbol (namespace handler)))
     (with-redefs [~handler (stub stub-key#)]
       (routes/handler {:uri ~path :request-method ~method})
       (should-have-invoked stub-key#)
       (reset! args (stub/first-invocation-of stub-key#)))))

(defmacro test-route [path method handler & body]
  `(it ~path
     (check-route ~path ~method ~handler)
     ~@body))

(defmacro test-redirect [path method dest]
  `(it (str ~path " -> " ~dest)
     (let [response# (routes/handler {:uri ~path :request-method ~method})]
       (wire-helper/should-redirect-to response# ~dest))))

(defmacro test-webs [id sym]
  `(it (str "remote " ~id " -> " '~sym)
     (let [action# (ws/resolve-handler ~id)]
       (should-not= nil action#)
       (should= '~sym (.toSymbol action#)))))

(describe "Routes"
  (with-stubs)
  (before (reset! args :none))
  (around [it] (with-redefs [c3kit.wire.api/version (constantly "fake-api-version")] (it)))

  ; Please keep these specs sorted alphabetically

  ;; web routes
  ; TODO - fixme
  (test-route "/" :get scattergories.layouts/web-rich-client)
  (test-route "/room/shrine" :get scattergories.layouts/web-rich-client)
  ;(test-route "/forgot-password" :get scattergories.layouts/web-rich-client)
  ;(test-route "/recover-password/foo" :get scattergories.layouts/web-rich-client)
  ;(test-route "/sandbox/example-page" :get scattergories.sandbox.core/handler)
  ;(test-route "/signout" :any scattergories.auth/web-signout)
  ;(test-route "/user/websocket" :any scattergories.auth/websocket-open)

  ;; ajax routes
  ;(test-route "/ajax/forgot-password" :post scattergories.auth/ajax-forgot-password)
  ;(test-route "/ajax/recover-password" :post scattergories.auth/ajax-recover-password)
  ;(test-route "/ajax/spinner" :get scattergories.routes/spinner)
  ;(test-route "/ajax/user/csrf-token" :get scattergories.auth/ajax-csrf-token)
  ;(test-route "/ajax/user/signin" :post scattergories.auth/ajax-login)

  ;; websocket handlers
  (test-webs :ws/close             scattergories.room/ws-leave-room)
  (test-webs :room/create          scattergories.room/ws-create-room)
  (test-webs :room/join            scattergories.room/ws-join-room)
  (test-webs :room/fetch           scattergories.room/ws-fetch-room)
  (test-webs :game/start           scattergories.game/ws-start-game)
  (test-webs :game/submit-answers  scattergories.game/ws-submit-answers)

  (it "not-found global - nil - handled by http"
    (let [response (routes/handler {:uri "/blah" :request-method :get})]
      (should-be-nil response)))

  (it "spinner"
    (with-redefs [routes/sleep-for-10 (stub :sleep)]
      (wire-helper/should-be-ajax-ok (routes/spinner :blah) nil)
      (should-have-invoked :sleep)))

  )