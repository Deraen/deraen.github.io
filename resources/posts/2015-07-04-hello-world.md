---
name: Hello World – Building a blog using Boot
keywords: boot, blog
---

I've been planning on starting a blog for some time already. But as I love
hacking with build tools and such I usually spend some days trying a blog
generator before deciding it's not good enough and start writing a new
one, without ever finishing anything. Thus I was happy when I saw
[Perun][perun] which does pretty much everything I want:

- Modular design, easy to extend
- No forced metadata schema
- ~~RSS~~ Atom feed
- Reads markdown
- Templating using Hiccup

## Perun

While working on a project I like to constantly see what is the result of
the code I'm working on. On ClojureScript I use [Figwheel][figwheel]. Thus
I wanted the same experience for working on blog, though this is not that
useful when writing it's useful while setting up the blog. To make this
workflow possible with Perun a two changes were implemented:

### Fast rebuilds

Parsing the markdown files and their metadata into Clojure data takes
relatively long. If the file has not been changed that work is
unnecessary. Boot's `watch` task and fileset provide an easy way to see
which files changed since the last build. Perun's `markdown`-task which
parses the files uses this to read only changed files and merge
the changed metadata to existing metadata from previous builds. This
provides build times of around 100ms when post content has been changed.

### Clojure changes

Templating in Perun is done from Clojure using Hiccup. To automatically
render the changes whenever the clj files change the render tasks use Boot
pods to run the code in fresh environment. This way the Clojure namespaces
are reloaded after the changes and changes are instantly seen. Because all
namespaces required by the render namespaces has to be loaded when files
change, it usually takes around 10 seconds to build the site after clj
file change.

Alternative approach would be to use [tools.namespace][tools.namespace] to
reload the changed namespaces. That should be quite a bit faster at the
expense of simplicity. This approach is used by
[boot-garden][boot-garden].

### Extendability

As mentioned Perun should be easy to extend. This is achieved by
implementing Perun using multiple Boot tasks which can be composed. Below
is a example from this blog's `build.boot` file which shows some examples
of the extendability. The tasks can be categorized in three types:

- Tasks which read metadata from files, currently only `markdown` (1)
- Tasks which manipulate the metadata (2)
- Tasks which render some output (3, 4)

```clj
(deftask split-keywords []
  (boot/with-pre-wrap fileset
    (->> fileset
         (perun/get-perun-meta)
         (perun/map-vals
           (fn [{:keys [keywords] :as post}]
             (if (string? keywords)
               (assoc post :keywords (->> (string/split keywords #",")
                                          (mapv string/trim)))
               post)))
         (perun/with-perun-meta fileset))))

(deftask build
  [p prod bool "Build rss, sitemap etc."]
  (comp (less :source-map true :compress prod)
        ;; 1
        (markdown)
        ;; 2
        (if prod (draft) identity)
        (slug)
        (permalink)
        (split-keywords)
        ;; 3
        (render :renderer 'blog.views.post/render)
        (collection :renderer 'blog.views.index/render :page "index.html")
        (collection :renderer 'blog.views.tags/render :page "tags.html")
        ;; 4
        (if prod (sitemap :filename "sitemap.xml") identity)
        (if prod
          (rss :title "Blog"
               :description "Deraen's blog"
               :link "http://deraen.github.io")
          identity)))
```

The task `split-keywords` is a task which manipulates the metadata by
splitting keyword strings. Though I now see that I should instead just
define the keywords as arrays in YAML metadata of the posts.

In this example there are five tasks which output files. A task which
renders all the posts, two tasks which render a collection view and
tasks for RSS and sitemap. The second collection task collects a list of
all of tags (keywords) in the posts and creates a tag cloud out of those,
what is cool here is that to create a tag page no special task was needed
as the render function can itself do the necessary work (one `reduce`).

## HTML and CSS livereload

Figwheel style ClojureScript development is already possible with
[Boot][boot-clj] using [boot-reload][boot-reload] but that is heavily
built for ClojureScript use cases. The client is written in Cljs so a
built step is required. Also boot-reload doesn't handle HTML file reloads
which are the most useful for static page development.

Previously I've been using [LiveReload.js][livereload.js] with
[Gulp.js][gulp] so I thought that should
be a good fit for Boot also. Luckily [a Clojure implementation][clj-livereload]
of LiveReload already existed. It required some API changes to make it
generally usable library. After the changes it was a breeze to create a [boot
task][boot-livereload].

## Cool features and possibilities

As it is easy to manipulate the data between reading the files and
rendering there is great opportunity to move some features which are
usually implemented in the browser to build process. The value I see there
is that RSS readers (AFAIK) do not execute JS so those readers are left
without e.g. code highlighting.

Another possibility is to hyphenate the text. This is even more useful as
the hyphenation is relatively expensive operation so it's great if it only
needs to be executed once instead of each page load. For this reason
I already starting writing [clj-hyphenate][clj-hyphenate] which implements
Franklin M. Liang's hyphenation algorithm in Clojure. The algorithm is the
same as used by TeX, LibreOffice and Hyphenator.js. To hyphenate HTML it
is possible to insert soft-hyphens into the text which the browser only
shows if the word needs to be split into multiple lines.

[boot-clj]: http://boot-clj.com
[perun]: https://github.com/hashobject/perun
[livereload]: http://livereload.com/
[figwheel]: https://github.com/bhauman/lein-figwheel
[clj-livereload]: https://github.com/bhurlow/clj-livereload
[gulp]: http://gulpjs.com/
[livereload.js]: http://livereload.com
[boot-livereload]: https://github.com/Deraen/boot-livereload
[boot-reload]: https://github.com/adzerk-oss/boot-reload
[boot-garden]: https://github.com/martinklepsch/boot-garden
[tools.namespace]: https://github.com/clojure/tools.namespace
[clj-hyphenate]: https://github.com/Deraen/clj-hyphenate
