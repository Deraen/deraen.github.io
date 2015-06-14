(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[hiccup "1.0.5"]
                  [perun "0.1.0-SNAPSHOT"]
                  [clj-time "0.9.0"]
                  [deraen/boot-less "0.4.0"]
                  [deraen/boot-livereload "0.1.0-SNAPSHOT"]
                  [pandeiro/boot-http "0.6.3-SNAPSHOT"]

                  [org.webjars/bootstrap "3.3.4"]])

(require '[io.perun :refer :all]
         '[blog.views.index :as index-view]
         '[blog.views.post :as post-view]
         '[pandeiro.boot-http :refer [serve]]
         '[deraen.boot-less :refer [less]]
         '[deraen.boot-livereload :refer [livereload]])

(deftask build
  "Build blog."
  [p prod  bool "Build rss, sitemap etc."]
  (comp (less :source-map true :compress prod)
        (markdown)
        (if prod (draft) identity)
        (ttr)
        (permalink)
        (render :renderer post-view/render)
        (collection :renderer index-view/render :page "index.html" :comparator (fn [i1 i2] (compare i2 i1)))
        (if prod (sitemap :filename "sitemap.xml") identity)
        (if prod (rss :title "Blog" :description "Deraen's blog" :link "http://deraen.github.io") identity)
        ))

(deftask dev
  []
  (comp (watch)
        (build)
        ; TODO: Create Liverelaod.js compatible task
        (livereload :asset-path "public")
        (serve :resource-root "public")))
