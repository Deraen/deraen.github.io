(ns blog.views.atom
  (:require [clojure.data.xml :as xml]
            [blog.dates :refer [datestr iso-datetime]]
            [blog.views.common :as common]))

(defn updated [{:keys [date-modified date-published date-created]}]
  (or date-modified date-published date-created))

(defn render
  [{:keys [author base-url site-title]}
   posts]
  (xml/emit-str
    (xml/sexp-as-element
      [:feed {:xmlns "http://www.w3.org/2005/Atom"}
       [:title site-title]
       [:link {:href (str base-url "atom.xml") :rel "self"}]
       [:link {:href base-url}]
       [:updated (->> (take 10 posts)
                      (map updated)
                      (apply max)
                      iso-datetime)]
       [:id base-url]
       [:author
        [:name (:name author)]
        [:email (:email author)]]
       (for [{:keys [canonical-url content name] :as post} (take 10 posts)]
         [:entry
          [:title name]
          [:link canonical-url]
          [:updated (iso-datetime (updated post))]
          [:content {:type "html"} (str content)]
          ])])))
