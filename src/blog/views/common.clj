(ns blog.views.common
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]))

(defn ga []
  nil)

(defn header []
  [:nav#main-nav {:role "navigation"}
   [:div.header
    [:a#logo {:href "/"}]]])

(defn footer []
  [:footer.row])

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
   (include-css "http://fonts.googleapis.com/css?family=PT+Sans")
   (ga)])
