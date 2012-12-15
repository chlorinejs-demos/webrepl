# Chlorine Web REPL

Web REPL for Clojure/ChlorineJS

Chlorine (formerly Clojurejs) is a naive implementation of a Clojure subset language to Javascript translator.

```bash
lein deps
lein run
```
See it online at: http://web-repl.herokuapp.com/index.html

You will see a terminal box, then:
```
$ (import! [:private "boot.cl2"])
$ (include-core!) ;; or (include-core-macros!)
```
to bootstrap Chlorine core language. Now check this:
```
$ (map inc (range 200))
```
and see the result. Remember, the script is run in **your** browser. You have been warned :)

For more information see the chlorine [wiki](https://github.com/myguidingstar/chlorine/wiki).

## License

Copyright (C) 2012 Hoang Minh Thang

Distributed under the Eclipse Public License, the same as Clojure.

