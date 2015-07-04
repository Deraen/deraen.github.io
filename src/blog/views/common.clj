(ns blog.views.common
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.util :refer [url-encode]]))

(defn ga []
  [:script
"(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

ga('create', 'UA-36245688-2', 'auto');
ga('send', 'pageview');"])

(defn header []
  [:nav.header {:role "navigation"}
   [:div.container
    [:a.site {:href "/"} "Deraen's blog"]
    [:ul
     [:li [:a {:href "/tags.html"} "tags"]]]]])

(defn footer []
  [:footer.footer
   [:div.container
    [:ul
     [:li [:a {:href "/atom.xml"} "RSS"]]
     [:li [:a {:href "https://github.com/Deraen"} "github.com/Deraen"]]
     [:li [:a {:href "https://twitter.com/JuhoTeperi"} "twitter.com/JuhoTeperi"]]
     [:li [:a {:href "http://metosin.fi"} "metosin.fi"]]]]])

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
