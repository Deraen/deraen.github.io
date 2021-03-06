(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[org.clojure/clojure "1.8.0" :scope "provided"]
                  [hiccup "1.0.5"]
                  [perun "0.4.0-20160730.220016-2"]
                  [clj-time "0.12.0"]
                  [deraen/boot-less "0.5.0"]
                  [deraen/boot-livereload "0.1.2"]
                  [deraen/boot-hyphenate "0.1.0"]
                  [pandeiro/boot-http "0.7.3"]
                  [org.slf4j/slf4j-nop "1.7.21"]

                  [org.webjars.npm/normalize.css "3.0.3"]
                  [org.webjars.npm/highlight.js "8.7.0"]])

(require '[io.perun :refer :all]
         '[pandeiro.boot-http :refer [serve]]
         '[deraen.boot-less :refer [less]]
         '[deraen.boot-livereload :refer [livereload]]
         '[deraen.boot-hyphenate :refer [hyphenate-html]]
         '[boot.core :as boot]
         '[clojure.string :as string]
         '[io.perun.core :as perun])

(deftask split-keywords []
  (boot/with-pre-wrap fileset
    (->> fileset
         (perun/get-meta)
         (map (fn [{:keys [keywords] :as post}]
                (if (string? keywords)
                  (assoc post :keywords (->> (string/split keywords #",")
                                             (mapv string/trim)))
                  post)))
         (perun/set-meta fileset))))

(deftask build
  [p prod bool "Build rss, sitemap etc."]
  (comp (less :source-map (not prod) :compression prod)
        (markdown :options {:extensions {:extanchorlinks true}})
        (global-metadata)
        (if prod (draft) identity)
        (slug)
        (permalink :permalink-fn #(str (:slug %) "/"))
        (canonical-url)
        (split-keywords)
        (render :renderer 'blog.views.post/render)
        (collection :renderer 'blog.views.index/render :page "index.html")
        (collection :renderer 'blog.views.tags/render :page "tags/index.html")
        (hyphenate-html :remove #{#"^public/tags/index.html$" #"^public/index.html"} :language "en-gb")
        (atom-feed
         :filterer :original
         :filename "atom.xml")
        (if prod (sitemap :filename "sitemap.xml") identity)
        ))

(deftask dev []
  (comp (repl :server true)
        (watch)
        (build)
        (livereload :asset-path "public" :filter #"\.(css|html|js)$")
        (serve :resource-root "public")))

(deftask prod []
  (comp (build :prod true)
        (sift :include #{#"^public"})
        (sift :move {#"^public/" ""})
        (target :dir #{"build"})))
