(ns blog.views.index
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [blog.dates :as dates]
            [blog.views.common :as common]))

(defn render-post
  [{:keys [filename name description
           ttr date_published
           author author_email author_avatar]}]
  [:li.item {:itemprop "blogPost" :itemscope "" :itemtype "http://schema.org/BlogPosting"}
   [:a.title {:href (str filename) :itemprop "name"} name]
   [:div.item-meta
    [:meta {:itemprop "author" :content (str author " (" author_email ")" )}]
    (if author_avatar
      [:img.author-avatar {:src author_avatar :title author}])
    [:p.pub-data (str (dates/format-datestr date_published "MMM dd, YYYY") ", by " author)
     [:span.reading-time (str " " ttr " mins read")]]
    [:p {:itemprop "description"} description]]])


(defn render [posts]
  (html5
    {:lang "en" :itemtype "http://schema.org/Blog"}
    (into
      (common/head)
      [; [:meta {:itemprop "author" :name "author" :content ""}]
       ; [:meta {:name "keywords" :itemprop "keywords" :content ""}]
       ; [:meta {:name "description" :itemprop "description" :content ""}]
       [:title "Blog"]])
    [:body
     (common/header)
     [:div.row.content
      [:ul.items.columns.small-12
       (for [post posts] (render-post post))]]
     (common/footer)]))
