(ns app.parser
  (:require
   [app.resolvers :refer [person-resolver list-resolver friends-resolver enemies-resolver]]
   [app.mutations :refer [delete-person]]
   [com.wsscode.pathom3.interface.eql :as p.eql]
   [com.wsscode.pathom3.connect.indexes :as pci]
   [com.wsscode.pathom.viz.ws-connector.pathom3 :as p.connector]
   [taoensso.timbre :as log]))

(def env
  (p.connector/connect-env (pci/register [person-resolver list-resolver friends-resolver enemies-resolver delete-person]) "Site 3000"))

(defn api-parser [query]
  (log/info "Process" query)
  (p.eql/process env query))


