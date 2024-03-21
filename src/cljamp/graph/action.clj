(ns cljamp.graph.action)

(defprotocol Protocol

  (graph-name
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
  [graph-name spec tests func]

  Protocol

  (graph-name [_] graph-name)


  (spec [_] spec)


  (tests [_] tests)


  (func [_] func))

(defn ->action
  [graph-name spec tests func]
  (->Type graph-name spec tests func))
