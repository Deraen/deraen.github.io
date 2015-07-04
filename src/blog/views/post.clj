(ns blog.views.post
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [blog.dates :refer [datestr]]
            [blog.views.common :as common]))

(defn render
  [{:keys [author author-email author-url location
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
       (if in-language [:meta {:itemprop "inLanguage" :content in-language}])
       [:meta {:itemprop "dateCreated" :content date-created}]
       (if date-modified [:meta {:itemprop "dateModified" :content date-modified}])
       (if date-published [:meta {:itemprop "datePublished" :content date-published}])
       [:title {:itemprop "name"} name]
       [:link {:rel "discussionUrl" :href discussion-url}]
       ; [:link {:rel "canonical" :href canonical-url}]
       ])
    [:body
     (common/header)
     [:div.main
      [:div.container
       [:article
        [:h1 {:itemprop "name"} name]
        [:div.meta (datestr date-published "dd MMMM YYYY")]
        (str content)
        [:aside
         (if date-modified
           [:div.meta
            [:span.meta-label "Modified: "]
            [:span (datestr date-modified "dd MMMM YYYY")]])
         (if keywords
           [:div.meta
            [:span.meta-label "Keywords: "]
            [:ul.keywords
             (for [k keywords]
               [:li [:a {:href (str "/tags.html#" k)} k]])]])]]]]
     (common/footer)]))
