(ns app.application
  (:require
    [com.fulcrologic.fulcro.react.version18 :refer [with-react18]]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.networking.http-remote :as http]))

(defonce app (with-react18 (app/fulcro-app
                {:remotes {:remote (http/fulcro-http-remote {})}})))
