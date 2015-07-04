(ns blog.views.atom
  (:require [clojure.data.xml :as xml]
            [blog.dates :refer [datestr]]
            [blog.views.common :as common]))

(defn render [posts]
  (xml/emit-str
    (xml/sexp-as-element
      [:feed {:xmlns "http://www.w3.org/2005/Atom"}
       [:title "Deraen's blog"]
       [:link {:href "http://deraen.github.io/atom.xml" :rel "self"}]
       [:link {:href "http://deraen.github.io"}]
       ; [:updated "fixme"]
       [:id "http://deraen.github.io"]
       [:author
        [:name "Juho Teperi"]
        [:email "juho.teperi@iki.fi"]]
       (for [{:keys [permalink content name date-published]} (take 10 posts)]
         [:entry
          [:title name]
          [:link permalink]
          ; (if date-updated [:updated date-updated])
          [:content {:type "html"} (str content)]
          ])])))
