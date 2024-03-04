.PHONY: test
test:
	clojure -M:test test

.PHONY: kibit
kibit:
	clojure -M:test:kibit --paths src,test

.PHONY: kondo
kondo:
	clojure -M:test:kondo --lint src test --paralell --cache false

.PHONY: eastwood
eastwood:
	clojure -M:test:eastwood

.PHONY: cljfmt-check
cljfmt-check:
	clojure -M:test:cljfmt check

.PHONY: cljfmt-fix
cljfmt-fix:
	clojure -M:test:cljfmt fix

.PHONY: cljstyle-check
cljstyle-check:
	cljstyle check

.PHONY: cljstyle-fix
cljstyle-fix:
	cljstyle fix

.PHONY: run-dev
run-dev:
	clj -A:dev:test

.PHONY: clj-deps
clj-deps:
	clj -X:deps prep 

.PHONY: build
build:
	clj -T:build jar

.PHONY: all-checks
all-checks: cljfmt-check cljstyle-check kibit kondo eastwood test