(ns app.ui
  (:require
   [app.mutations :as api]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(defsc Person [this {:person/keys [id name age] :as props} {:keys [onDelete]}]
  {:query [:person/id :person/name :person/age]
   :ident (fn [] [:person/id (:person/id props)])}
  (dom/li
   (dom/h5 (str name " (age: " age ")") (dom/button {:onClick #(onDelete id)} "X")))) ; (4)

(def ui-person (comp/factory Person {:keyfn :person/id}))

(defsc PersonList [this {:list/keys [id people] :as props}]
  {:query [:list/id {:list/people (comp/get-query Person)}]
   :ident (fn [] [:list/id (:list/id props)])}
  (let [delete-person (fn [person-id] (comp/transact! this [(api/delete-person {:list/id id :person/id person-id})]))] ; (2)
    (dom/div
     (dom/ul
      (map #(ui-person (comp/computed % {:onDelete delete-person})) people)))))

(def ui-person-list (comp/factory PersonList))

(defsc Root [this {:keys [friends enemies]}]
  {:query         [{:friends (comp/get-query PersonList)}
                   {:enemies (comp/get-query PersonList)}]
   :initial-state {}}
  (dom/div
   (dom/h3 "Friends")
   (when friends
     (ui-person-list friends))
   (dom/h3 "Enemies")
   (when enemies
     (ui-person-list enemies))))

(comment
  (js/alert "hello")
  ; check data tree (source)
  (def state (com.fulcrologic.fulcro.application/current-state app.application/app))
  (def query (com.fulcrologic.fulcro.components/get-query app.ui/Root))
  (com.fulcrologic.fulcro.algorithms.denormalize/db->tree query state state)

  ; check normalized form/data
  (com.fulcrologic.fulcro.application/current-state app.application/app)
  (com.fulcrologic.fulcro.algorithms.denormalize/db->tree [{:friends [:list/label]}] (comp/get-initial-state app.ui/Root {}) {})
  (com.fulcrologic.fulcro.components/get-initial-state app.ui/Root {})
  (meta (com.fulcrologic.fulcro.components/get-query app.ui/PersonList)))

;; npx shadow-cljs server
;; npx shadow-cljs cljs-repl main
;;
;; The query and the ident work together to do normalization.
;;
;; com.fulcrologic.fulcro.algorithms.normalize/tree→db that can simultaneously walk a data tree (in this case initial-state) and a component-annotated query.
;; When it reaches a data node whose query metadata names a component with an ident it places that data into the appropriate
;; table (by calling your ident function), and replaces the data in the tree with its ident.
