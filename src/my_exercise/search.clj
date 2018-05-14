(ns my-exercise.search
  (:require [hiccup.page :refer [html5]]
            [clojure.string :as s]))

(defn format-place
  "Formats place name string to OCD ID style."
  [place]
  (s/lower-case (s/replace place #" " "_")))

(defn format-state
  "Formats state abbreviation to OCD ID style."
  [state]
  (s/lower-case state))

(defn generate-ocd-ids
  "Generates a vector of OCD IDs for given address inputs."
  [city state]
  (let [ocd-ids []
        country-division "ocd-division/country:us"
        state-division (str country-division "/state:" (format-state state))
        place-division (str state-division "/place:" (format-place city))]
    (cond-> ocd-ids
      true (conj country-division)
      (not (s/blank? state))
      (conj state-division)
      (not (s/blank? city))
      (conj place-division))))

(defn header [_]
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0, maximum-scale=1.0"}]
   [:title "Find my next election"]
   [:link {:rel "stylesheet" :href "default.css"}]])

(defn results
  "HTML body of the search results page."
  [request]
  [:div {:class "results"}
   [:h1 "Upcoming Elections"]
   [:p (str (generate-ocd-ids (get-in request [:params :city]) (get-in request [:params :state])))]])

(defn page [request]
  (html5
   (header request)
   (results request)))