(ns my-exercise.search
  (:require [hiccup.page :refer [html5]]
            [clj-http.client :as client]
            [clojure.edn :as edn]
            [clojure.string :as s])
            (:import [java.text SimpleDateFormat]))

(defn format-place
  "Formats place name string to OCD ID style."
  [place]
  (s/lower-case (s/replace place #" " "_")))

(defn format-state
  "Formats state abbreviation to OCD ID style."
  [state]
  (s/lower-case state))

(defn format-date
  "Format election date displayed on page."
  [date]
  (.format (SimpleDateFormat. "MM/dd/yyyy") date))

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

(defn upcoming-elections
  "Get upcoming elections from Turbovote API for given address inputs."
  [city state]
  (let [ocd-ids (s/join "," (generate-ocd-ids city state))]
    (edn/read-string (:body (client/get "https://api.turbovote.org/elections/upcoming"
                                        {:query-params {"district-divisions" ocd-ids}})))))

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
  (let [elections (upcoming-elections (get-in request [:params :city]) (get-in request [:params :state]))]
    [:div {:class "results"}
     [:h1 "Upcoming Elections"]
     (if (empty? elections)
       [:p "No Results"]
       [:ul (for [election elections]
              [:li (format-date (:date election)) " - " [:a {:href (:website election)} (:description election)]])])]))

(defn page [request]
  (html5
   (header request)
   (results request)))