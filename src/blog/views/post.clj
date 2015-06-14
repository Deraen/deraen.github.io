(ns blog.views.post
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [blog.dates :as dates]
            [blog.views.common :as common]))

(defn render
  [{:keys [author_avatar author author_email author_url location
           in_language ttr
           date_created date_modified date_published
           name content keywords description
           discussion_url canonical_url]}]
  (println author_avatar)
  (html5
    {:lang in_language :itemscope "" :itemtype "http://schema.org/BlogPosting"}
    (into
      (common/head)
      [[:meta {:itemprop "author" :name "author" :content (str author " (" author_email ")" )}]
       [:meta {:name "keywords" :itemprop "keywords" :content keywords}]
       [:meta {:name "description" :itemprop "description" :content description}]
       [:meta {:itemprop "inLanguage" :content in_language}]
       [:meta {:itemprop "dateCreated" :content date_created}]
       [:meta {:itemprop "dateModified" :content date_modified}]
       [:meta {:itemprop "datePublished" :content date_published}]
       [:title {:itemprop "name"} name]
       [:link {:rel "discussionUrl" :href discussion_url}]
       [:link {:rel "canonical" :href canonical_url}]])
    [:body
     (common/header)
     [:div.row.content
      [:div.post.small-12.columns
       [:h1 {:itemprop "name"} name]
       (str content)
       [:aside.post-meta.small-12.medium-12.columns
        (if author_avatar
          [:img.author-avatar {:src author_avatar :title author}])
        [:div.meta-info
         [:div
          [:span.meta-label "Written by: "]
          [:a.author-name {:href author_url} author]]
         [:div
          [:span.meta-label "Published: "]
          [:span (dates/reformat-datestr date_published "YYYY-MM-dd", "MMM dd, YYYY")]]
         [:div
          [:span.meta-label "Modified: "]
          [:span (dates/reformat-datestr date_modified "YYYY-MM-dd", "MMM dd, YYYY")]]
         [:div
          [:span.meta-label "Published in: "]
          [:span location]]
         [:div
          [:span.meta-label "Reading time: "]
          [:span (str ttr " mins")]]]]]]
     (common/footer)]))
