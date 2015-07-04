(ns blog.views.common
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.util :refer [url-encode]]))

(defn ga []
  nil)

(defn header []
  [:nav.header {:role "navigation"}
   [:div.container
    [:a.site {:href "/"} "Deraen's blog"]
    [:ul
     [:li [:a {:href "/tags.html"} "tags"]]]]])

(defn footer []
  [:footer.footer
   [:div.container]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0, user-scalable=no"}]
   [:link {:rel "shortcut icon" :href "/favicon.ico"}]
   ; [:link {:rel "publisher" :href ""}]
   ; [:link {:rel "author" :href "humans.txt"}]
   [:link {:rel "alternate" :type "application/rss+xml" :title "RSS" :href "/feed.rss"}]
   (include-css "/css/app.css")
   (include-css (str "http://fonts.googleapis.com/css?family=" (url-encode "Lato|Source+Code+Pro|Merriweather:400,700")))
   (include-js "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/highlight.min.js")
   (include-js "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.6/languages/clojure.min.js")
   [:script "hljs.initHighlightingOnLoad();"]
   (ga)])
