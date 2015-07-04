(ns blog.views.post
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [clojure.string :as string]
            [blog.dates :refer [datestr]]
            [blog.views.common :as common]))

(defn render
  [global
   {:keys [author author-email author-url location
           in-language ttr
           date-created date-modified date-published
           name content keywords description
           discussion-url canonical-url]}]
  (html5
    {:lang in-language :itemscope "" :itemtype "http://schema.org/BlogPosting"}
    (into
      (common/head global)
      [[:meta {:itemprop "author" :name "author" :content (str (or author (:name (:author global))) " (" (or author-email (:email (:author global))) ")" )}]
       [:meta {:name "keywords" :itemprop "keywords" :content (string/join ", " keywords)}]
       [:meta {:name "description" :itemprop "description" :content description}]
       (if-let [l (or in-language (:language global))] [:meta {:itemprop "inLanguage" :content l}])
       [:meta {:itemprop "dateCreated" :content date-created}]
       (if date-modified [:meta {:itemprop "dateModified" :content date-modified}])
       (if date-published [:meta {:itemprop "datePublished" :content date-published}])
       [:title {:itemprop "name"} name " - " (:site-title global)]
       ; FIXME:
       ; [:link {:rel "discussionUrl" :href discussion-url}]
       [:link {:rel "canonical" :href canonical-url}]])
    [:body
     (common/header global)
     [:div.main
      [:div.container
       [:article
        [:h1 {:itemprop "name"} name]
        [:div.meta (datestr date-published "dd MMMM YYYY")]
        (str content)
        [:aside
         (if keywords
           [:div.meta
            [:span.meta-label "Tags: "]
            [:ul.keywords
             (for [k keywords]
               [:li [:a {:href (str "/tags/#" k)} k]])]])]]]]
     (common/footer)]))
