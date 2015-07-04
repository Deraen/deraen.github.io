(ns blog.views.atom
  (:require [clojure.data.xml :as xml]
            [blog.dates :refer [datestr]]
            [blog.views.common :as common]))

(defn render
  [{:keys [author base-url site-title]}
   posts]
  (xml/emit-str
    (xml/sexp-as-element
      [:feed {:xmlns "http://www.w3.org/2005/Atom"}
       [:title site-title]
       [:link {:href (str base-url "atom.xml") :rel "self"}]
       [:link {:href base-url}]
       ; [:updated "fixme"]
       [:id base-url]
       [:author
        [:name (:name author)]
        [:email (:email author)]]
       (for [{:keys [canonical-url content name date-published]} (take 10 posts)]
         [:entry
          [:title name]
          [:link canonical-url]
          ; (if date-updated [:updated date-updated])
          [:content {:type "html"} (str content)]
          ])])))
