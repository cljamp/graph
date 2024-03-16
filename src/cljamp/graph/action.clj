(ns cljamp.graph.action)

(defprotocol Protocol

  (node-name
    [action]
    "Return action (node) name")

  (spec
    [action]
    "Return both of args and return spec")

  (tests
    [action]
    "Return tests for function")

  (func
    [action]
    "Return action function"))

(defrecord Type
           [node-name spec tests func]

  Protocol

  (node-name [_] node-name)


  (spec [_] spec)


  (tests [_] tests)


  (func [_] func))

(defn ->action
  [node-name spec tests func]
  (->Type node-name spec tests func))
