# Chlorine Web REPL

Web REPL for Clojure/ChlorineJS

Chlorine (formerly Clojurejs) is a naive implementation of a Clojure subset language to Javascript translator.

```bash
lein deps
lein run
```

See it online at: http://web-repl.herokuapp.com/
or http://webrepl-myguidingstar.rhcloud.com/

You will see a terminal box, just check this:
```
$ (map inc (range 200))
```
and see the result. Remember, the script is run in **your** browser. You have been warned :)

For more information see the chlorine [wiki](https://github.com/chlorinejs/chlorine/wiki).

## Note
WebREPL takes time to compile which may exceed cloud services' timeout to start applications.
Deploying war files is often a better choice.

## License

Copyright (C) 2012 Hoang Minh Thang

Distributed under the Eclipse Public License, the same as Clojure.

