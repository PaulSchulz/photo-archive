(ns photo-archive.components.gallery
    (:require [reagent.core :as reagent]))

(defn gallery-item [{:keys [id thumbnail title]} on-click]
  [:div.gallery-item {:key id
                      :on-click #(on-click id)}
   [:img {:src thumbnail
          :alt title}]
   [:div.gallery-item-title title]])

(defn gallery [photos on-photo-select]
  [:div.gallery-container
   [:div.gallery-grid
    (doall (map-indexed (fn [idx photo]
                          (with-meta
                            [gallery-item photo on-photo-select]
                            {:key (:id photo)}))
                        photos))]])
