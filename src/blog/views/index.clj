(ns blog.views.index
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [blog.dates :refer [datestr]]
            [blog.views.common :as common]))

(defn render [posts]
  (html5
    {:lang "en" :itemtype "http://schema.org/Blog"}
    (into
      (common/head)
      [; [:meta {:itemprop "author" :name "author" :content ""}]
       ; [:meta {:name "keywords" :itemprop "keywords" :content ""}]
       ; [:meta {:name "description" :itemprop "description" :content ""}]
       [:title "Deraen's blog"]])
    [:body
     (common/header)
     [:div.main
      [:div.container
       (for [{:keys [permalink name date-published]} posts]
         [:article {:itemprop "blogPost" :itemscope "" :itemtype "http://schema.org/BlogPosting"}
          [:h3
           [:span (datestr date-published)]
           " "
           [:a.title {:href permalink :itemprop "name"}
            name]]])]]
     (common/footer)]))
