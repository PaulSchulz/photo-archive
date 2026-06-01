(ns photo-archive.components.gallery
    (:require [reagent.core :as reagent]))

(defn gallery-item [{:keys [id thumbnail title]} on-click]
  ^{:key id}
  [:div.gallery-item {:on-click #(on-click id)}
   [:img {:src thumbnail
          :alt title}]
   [:div.gallery-item-title title]])

(defn gallery [photos on-photo-select]
  [:div.gallery-container
   [:div.gallery-grid
    (doall (for [photo photos]
             [gallery-item photo on-photo-select]))]])
