(ns blog.views.tags
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [clojure.string :as string]
            [blog.dates :refer [datestr]]
            [blog.views.common :as common]))

(defn log-scale [min max n m]
  (+ min (* (- max min) (/ (Math/log n) (Math/log m)))))

(defn tag-comparator [[tag-a a] [tag-b b]]
  (or (> (:n a) (:n b))
      (and (= (:n a) (:n b))
           (compare tag-a tag-b))))

(defn render [posts]
  (let [posts-by-tags
        (->> posts
             (reduce
               (fn [acc post]
                 (reduce (fn [acc keyword]
                           (-> acc
                               (update-in [keyword :n] (fnil inc 0))
                               (update-in [keyword :posts] (fnil conj []) post)))
                         acc
                         (:keywords post)))
               {})
             (sort tag-comparator))
        m (apply max (map (comp :n val) posts-by-tags))]
    (html5
      {:lang "en" :itemtype "http://schema.org/Blog"}
      (into
        (common/head)
        [; [:meta {:itemprop "author" :name "author" :content ""}]
         ; [:meta {:name "keywords" :itemprop "keywords" :content ""}]
         ; [:meta {:name "description" :itemprop "description" :content ""}]
         [:title "Blog"]])
      [:body
       (common/header)
       [:div.main
        [:div.container
         [:ul.tag-cloud
          (for [[tag {:keys [n]}] posts-by-tags]
            [:li.tag
             [:a {:href (str "#" tag)
                  :style (str "font-size: " (log-scale 100 200 n m) "%")}
              tag]])]

         (for [[tag {:keys [posts]}] posts-by-tags]
           [:div
            [:h3 [:a {:name tag}] tag]
            [:ul
             (for [{:keys [permalink name]} (sort-by :name posts)]
               [:li [:a {:href permalink} name]])]])]]
       (common/footer)])))
