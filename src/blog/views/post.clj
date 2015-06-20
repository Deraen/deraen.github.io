(ns blog.views.post
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [blog.dates :as dates]
            [blog.views.common :as common]))

(defn render
  [{:keys [author-avatar author author-email author-url location
           in-language ttr
           date-created date-modified date-published
           name content keywords description
           discussion-url canonical-url]}]
  (html5
    {:lang in-language :itemscope "" :itemtype "http://schema.org/BlogPosting"}
    (into
      (common/head)
      [[:meta {:itemprop "author" :name "author" :content (str author " (" author-email ")" )}]
       [:meta {:name "keywords" :itemprop "keywords" :content keywords}]
       [:meta {:name "description" :itemprop "description" :content description}]
       [:meta {:itemprop "inLanguage" :content in-language}]
       [:meta {:itemprop "dateCreated" :content date-created}]
       [:meta {:itemprop "dateModified" :content date-modified}]
       [:meta {:itemprop "datePublished" :content date-published}]
       [:title {:itemprop "name"} name]
       [:link {:rel "discussionUrl" :href discussion-url}]
       [:link {:rel "canonical" :href canonical-url}]])
    [:body
     (common/header)
     [:div.row.content
      [:div.post.small-12.columns
       [:h1 {:itemprop "name"} name]
       (str content)
       [:aside.post-meta.small-12.medium-12.columns
        (if author-avatar
          [:img.author-avatar {:src author-avatar :title author}])
        [:div.meta-info
         [:div
          [:span.meta-label "Written by: "]
          [:a.author-name {:href author-url} author]]
         [:div
          [:span.meta-label "Published: "]
          [:span (dates/format-datestr date-published "MMM dd, YYYY")]]
         [:div
          [:span.meta-label "Modified: "]
          [:span (dates/format-datestr date-modified "MMM dd, YYYY")]]
         [:div
          [:span.meta-label "Published in: "]
          [:span location]]
         [:div
          [:span.meta-label "Reading time: "]
          [:span (str ttr " mins")]]]]]]
     (common/footer)]))
