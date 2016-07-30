---
name: Boot ClojureScript tooling updates and what's up next
keywords: boot, cljs
date-created: 2015-10-04
date-published: 2015-10-04
uuid: 595f3d1b-b60d-46c7-9a06-d0dd6c220a01
---

One of the features missing from Boot ClojureScript tooling in comparison
to [Figwheel] was heads-up display (HUD). Thanks to [Martin Klepsch](https://twitter.com/martinklepsch)
this is now implemented in Boot-cljs and Boot-reload. I've made a screen cast
about the new feature so check that to see how it works. Read on for
some details about the implementation and to see what's up next for Boot
ClojureScript tooling.

## Demonstration

<div class="video-wrapper">
  <iframe width="1280" height="720" src="https://www.youtube-nocookie.com/embed/QQ3J59AKZLU" frameborder="0" allowfullscreen></iframe>
</div>

## Implementation

As Boot ClojureScript tooling consists of multiple separate tasks, implementation
of HUD required changes to two tasks:

### Boot-cljs

Implementation of HUD requires that information about ClojureScript warnings and
exceptions is available for sending to the client.

To catch the information
about Cljs warnings we'll set up custom warning-handler. The handler with both
*(1)* print the warning to console and *(2)* store the warnings in an atom.
The reason why we overwrite default warning handler which would also print the
warnings to console, is that we want to process file-path of the warning
before printing. Because ClojureScript sees to source files at Boot
temporary-directories the file paths include the temporary directory path.
To make the warnings cleaner we *(3)* retrieve path for the original file.
Data about warnings is attached to `.cljs.edn` file metadata on the fileset.

```clj
(fn [warning-type env extra]
  (when (warning-enabled? warning-type)
    (when-let [s (ana/error-message warning-type extra)]
                 ;; 3
      (let [path (util/find-original-path source-paths dirs ana/*cljs-file*)]
        ;; 1
        (butil/warn "WARNING: %s %s\n" s (when (:line env)
                                           (str "at line " (:line env) " " path)))
        ;; 2
        (swap! warnings conj {:message s
                              :file path
                              :line (:line env)
                              :type warning-type})))))
```

Handling exceptions is a bit more trickier because ClojureScript compiler
is running inside a [pod][pods]. Usually the communication between pods happens
using `pr-str` and `read-string`, very similarly to how Leiningen communicates
between multiple JVMs. But this doesn't happen with exceptions, they are instead
directly thrown. The problem here is that for some reason when exceptions are
thrown from one classloader to another, they lose their `ex-info` metadata.
For ClojureScript exceptions the metadata contain all the interesting data:
file-path, line number and column number.

[A workaround](https://github.com/adzerk-oss/boot-cljs/blob/fd913b9c9a2bd9d51d28f855baadd225198950a2/src/adzerk/boot_cljs/util.clj#L47-L104)
I found for this is to manually serialize and deserialize exceptions, including
metadata, stack-trace and cause stack. This way it's possible to throw
exception with correct metadata from Boot-cljs to Boot-reload.

As with warnings, file paths in exceptions are changed to contain path to the
original file instead of to a file in temporary directory.

### Boot-reload

Boot-reload will either read warnings from `.cljs.edn` file metadata on the
fileset or catch the exceptions thrown by Boot-cljs. Because Boot tasks are
implemented using middleware pattern it's simple as just calling `next-task`
inside `try-catch`. Boot-cljs will tag the exceptions so that we can display
only the *(1)* interesting exceptions on the browser. All exceptions are
rethrown so that other tasks can access the exception data and to tell that
the build failed.

```clj
(try
  (next-task fileset)
  (catch Exception e
        ;; 1
    (if (= :boot-cljs (:from (ex-data e)))
      (send-visual! @pod {:exception (merge {:message (.getMessage e)}
                                            (ex-data e))}))
    (throw e)))
```

The heads-up display user interface is implemented purely using Google Closure
library. This keeps the build simpler as we don't need any additional dependencies.
Even though we are not using sophisticated framework like React, the user interface
implementation is only about one hundred lines, including CSS definitions.
UI is even implemented using immediate mode rendering: Whenever new `:visual`
message is received from the server, old DOM container is removed and a new one
is created.

## Next up

### Boot-reload fixes

Current file-reloading implementation has some problems when one has multiple
ClojureScript builds in one project. Boot-reload tries to load all changes JS
files in browser but it's possible that the files don't belong to the open
application and cause problems.

Google Closure library defines a (private) dependency graph of namespaces
and it should be possible to use that to determine if changed file is required
by any loaded namespace, if it's not, we don't need to load the changed file.

The same dependency graph can be used to sort the changed files in dependency order.
Currently Boot-cljs is calculating this dependency order and passing it to
Boot-reload, this is additional work as ClojureScript compiler has already done
this and passed the data to Closure.

Figwheel is already using Closure dependency data so I'll be looking on it's
implementation and copying relevant parts to Boot-reload.

### Boot-cljs-repl fixes and improvements

I have now working ClojureScript REPL setup with [Vim-fireplace][fireplace] so I'll be
fixing problems as I encounter them.

### Boot-cljs performance (fileset performance)

There have been multiple reports of Boot-cljs being slower than Leiningen Cljsbuild or Figwheel.
On most cases the ClojureScript compiler works just as fast, but Boot filesets
cause some overhead which shows especially with incremental recompiles.
Performance profiling should help to find the bottlenecks. Improving this
should help all Boot tasks.

[Figwheel]: https://github.com/bhauman/lein-figwheel
[pods]: https://github.com/boot-clj/boot/wiki/Pods
[fireplace]: https://github.com/tpope/vim-fireplace
