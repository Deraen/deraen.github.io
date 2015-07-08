(ns blog.dates
  (:require [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [clj-time.format :as tf]))

(defn datestr
  ([date] (datestr date "dd MMM YYYY"))
  ([date fmt] (tf/unparse (tf/formatter fmt) (tc/from-date date))))

(defn iso-datetime [date]
  (tf/unparse (tf/formatters :date-time-no-ms) (tc/from-date date)))
