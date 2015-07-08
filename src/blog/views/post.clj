(ns blog.views.post
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [clojure.string :as string]
            [blog.dates :refer [datestr]]
            [blog.views.common :as common]))

(defn disquss [id]
  [:div [:div#disqus_thread]
   [:script {:type "text/javascript"}
    (format
      "/* * * CONFIGURATION VARIABLES * * */
       var disqus_shortname = 'deraen';
       var disqus_identifier = '%s';

       /* * * DON'T EDIT BELOW THIS LINE * * */
       (function() {
       var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
       dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';
       (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
       })();" id)]
   [:noscript "Please enable JavaScript to view the <a href=\"https://disqus.com/?ref_noscript\" rel=\"nofollow\">comments powered by Disqus.</a></noscript>"]])

(defn render
  [global
   {:keys [author author-email author-url location
           in-language ttr
           date-created date-modified date-published
           permalink name content keywords description
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
               [:li [:a {:href (str "/tags/#" k)} k]])]])]
        [:aside.comments
         (disquss permalink)]]]]
     (common/footer)]))
