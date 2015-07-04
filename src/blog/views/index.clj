(ns blog.views.index
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [blog.dates :refer [datestr]]
            [blog.views.common :as common]))

(defn render [global posts]
  (html5
    {:lang "en" :itemtype "http://schema.org/Blog"}
    (common/coll-head global)
    [:body
     (common/header global)
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
