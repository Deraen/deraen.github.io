(ns blog.hyphenate
  (:require [clj-hyphenate.core :refer [hyphenate-paragraph hyphenate-word]]
            [clj-hyphenate.patterns.en-gb :as en]
            [boot.core :as b]
            [boot.util :as u]
            [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html]
            [clojure.string :as string]))

(defn hyphenate-p [{:keys [content] :as el}]

  (assoc el :content (map (fn [x]
                            (if (string? x)
                              ; Split-join loses whitespace - replace doesn't
                              (string/replace x #"[A-Za-z]+" #(hyphenate-word en/rules %))
                              x))
                          content)))

(defn hyphenate-html' [file]
  (-> file
      (html/html-resource)
      (html/transform [:p] hyphenate-p)
      html/emit*
      (->> (apply str))))

(b/deftask hyphenate-html []
  (let [prev (atom nil)
        out  (b/tmp-dir!)]
    (fn [next-task]
      (fn [fileset]
        (doseq [file (->> fileset
                          ; (b/fileset-diff @prev)
                          b/input-files
                          (b/by-ext [".html"]))
                :let [new-file (io/file out (b/tmp-path file))]]
          (u/info "Hyphenating file %s\n" (b/tmp-path file))
          (io/make-parents new-file)
          (spit new-file
                (hyphenate-html' (b/tmp-file file))))
        (reset! prev fileset)
        (next-task (-> fileset (b/add-resource out) b/commit!))))))
