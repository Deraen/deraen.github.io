(ns blog.dates
  (:require [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [clj-time.format :as tf]))

(defn format-datestr [date fmt]
  (tf/unparse (tf/formatter fmt) (tc/from-date date)))
