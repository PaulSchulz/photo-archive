(ns photo-archive.core
    (:require
              [reagent.core :as reagent :refer [atom]]
              [reagent.dom :as rd]
              [photo-archive.data :refer [sample-photos]]
              [photo-archive.components.gallery :refer [gallery]]
              [photo-archive.components.viewer :refer [photo-viewer]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:photos sample-photos
                          :selected-photo-id nil}))

(defn get-photo-by-id [photos id]
  (first (filter #(= (:id %) id) photos)))

(defn get-photo-index [photos id]
  (.indexOf (map :id photos) id))

(defn select-photo [id]
  (swap! app-state assoc :selected-photo-id id))

(defn close-viewer []
  (swap! app-state assoc :selected-photo-id nil))

(defn next-photo []
  (let [current-id (:selected-photo-id @app-state)
        photos (:photos @app-state)
        current-index (get-photo-index photos current-id)
        next-index (if (< current-index (- (count photos) 1))
                     (+ current-index 1)
                     0)
        next-photo (nth photos next-index)]
    (select-photo (:id next-photo))))

(defn prev-photo []
  (let [current-id (:selected-photo-id @app-state)
        photos (:photos @app-state)
        current-index (get-photo-index photos current-id)
        prev-index (if (> current-index 0)
                     (- current-index 1)
                     (- (count photos) 1))
        prev-photo (nth photos prev-index)]
    (select-photo (:id prev-photo))))

(defn app []
  (let [photos (:photos @app-state)
        selected-id (:selected-photo-id @app-state)
        selected-photo (when selected-id
                        (get-photo-by-id photos selected-id))]
    [:div.app
     [:header.app-header
      [:h1 "Photo Archive"]]
     [gallery photos select-photo]
     (when selected-photo
       [photo-viewer selected-photo close-viewer prev-photo next-photo])]))

(rd/render [app]
           (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
