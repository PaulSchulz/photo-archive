(ns photo-archive.components.viewer
    (:require [reagent.core :as reagent]))

(defn metadata-row [label value]
  [:div.metadata-row
   [:span.metadata-label label]
   [:span.metadata-value value]])

(defn metadata-panel [{:keys [title metadata]}]
  [:div.metadata-panel
   [:h2 title]
   [:div.metadata-content
    (metadata-row "Date" (:date metadata))
    (metadata-row "Camera" (:camera metadata))
    (metadata-row "ISO" (:iso metadata))
    (metadata-row "Aperture" (:aperture metadata))
    (metadata-row "Shutter Speed" (:shutter-speed metadata))
    (metadata-row "Focal Length" (:focal-length metadata))
    (metadata-row "Location" (:location metadata))
    [:div.metadata-row
     [:span.metadata-label "Description"]
     [:span.metadata-value (:description metadata)]]]])

(defn photo-viewer [{:keys [id url title metadata]} on-close on-prev on-next]
  [:div.viewer-overlay {:on-click on-close}
   [:div.viewer-container {:on-click #(.stopPropagation %)}
    [:button.viewer-close {:on-click on-close} "×"]
    [:button.viewer-nav.viewer-prev {:on-click on-prev} "‹"]
    [:button.viewer-nav.viewer-next {:on-click on-next} "›"]
    [:div.viewer-content
     [:div.viewer-image-container
      [:img.viewer-image {:src url
                         :alt title}]]
     [metadata-panel {:title title
                     :metadata metadata}]]]])
