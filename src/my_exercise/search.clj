(ns my-exercise.search
  (:require [hiccup.page :refer [html5]]))

(defn header [_]
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0, maximum-scale=1.0"}]
   [:title "Find my next election"]
   [:link {:rel "stylesheet" :href "default.css"}]])

(defn results
  "HTML body of the search results page."
  [_]
  [:div {:class "results"}
   [:h1 "Upcoming Elections"]])

(defn page [request]
  (html5
   (header request)
   (results request)))