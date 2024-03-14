(ns cljamp.graph.action)

(defprotocol Protocol

  (node-name
    [action]
    "Return action (node) name")

  (spec
    [action]
    "Return both of args and return spec")

  (func
    [action]
    "Return action function"))

(defrecord Type
  [node-name spec func]

  Protocol

  (node-name [_] node-name)


  (spec [_] spec)


  (func [_] func))

(defn ->action
  [node-name spec func]
  (->Type node-name spec func))
