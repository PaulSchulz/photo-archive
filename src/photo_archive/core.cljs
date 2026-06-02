(ns photo-archive.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rd]
   [photo-archive.filesystem :as fs]
   [photo-archive.manifest :as manifest]
   [photo-archive.components.gallery :refer [gallery]]
   [photo-archive.components.viewer :refer [photo-viewer]]))

(enable-console-print!)

;; App state
(defonce app-state (atom {:photos []
                          :selected-photo-id nil
                          :dir-handle nil
                          :loading false
                          :error nil}))

;; (reset! app-state {:directory dir-handle})

(defonce current-image (reagent/atom nil))

(defn show-image! [file-handle]
  (-> file-handle
      (.getFile)
      (.then
       (fn [file]
         (reset! current-image
                 (js/URL.createObjectURL file))))))

;; (defn image-view []
;;   (when-let [url @image-url]
;;     [:img
;;      {:src url
;;       :style {:max-width "100%"
;;               :max-height "80vh"}}]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn choose-directory! []
  (-> (js/window.showDirectoryPicker)
      (.then
       (fn [dir-handle]
         (js/console.log "Selected:" (.-name dir-handle))))
      (.catch
       (fn [err]
         (js/console.error err)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize directory and load photos
(defn load-photos-from-directory []
  ;; Change status to 'loading', and remove errors.
  (swap! app-state assoc :loading true :error nil)

  (-> (fs/request-directory-handle)
      (.then
       (fn [dir-handle]
         (swap! app-state assoc :dir-handle dir-handle)

         ;; Scan for images
         (-> (fs/scan-images dir-handle)
             (.then
              (fn [images]
                (if (empty? images)
                  (swap! app-state assoc :error "No images found in selected directory" :loading false)
                  (do
                    ;; Create map of filename -> file handle
                    (let [file-handles (into {} (map (fn [img] [(.-name img) (.-handle img)]) images))]

                      ;; Load or create manifest
                      (-> (manifest/load-or-create-manifest dir-handle images)
                          (.then
                           (fn [manifest-data]
                             ;; Load photo data URLs
                             (-> (manifest/load-photos-with-data-urls manifest-data file-handles)
                                 (.then
                                  (fn [photos-with-urls]
                                    ;; Save manifest
                                    (-> (manifest/save-manifest dir-handle manifest-data)
                                        (.then
                                         (fn []
                                           (swap! app-state assoc :photos (vec photos-with-urls)
                                                  :loading false))))))))))))))))
         (.catch (fn [e]
                   (js/console.error "Error scanning images:" e)
                   (swap! app-state assoc :error "Error scanning folder" :loading false))))))
  (.catch (fn [e]
            (js/console.error "Error accessing folder:" e)
            (swap! app-state assoc :error "Permission denied or operation cancelled" :loading false))))

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

(defn folder-selector []
  [:div.folder-selector
   [:div.folder-selector-content
    [:h2 "Photos"]
    [:p "Select a folder containing your photos"]
    [:button.btn-primary {:on-click load-photos-from-directory}
     "📁 Select Photo Folder"]
    (when-let [error (:error @app-state)]
      [:div.error-message error])
    (when (:loading @app-state)
      [:div.loading "Loading photos ..."])
    ]])

(defn photo-page []
  ;; [gallery photos select-photo]
  ;;  [:p "Photos"]
  [:img {:src "data/test-images/duckling-on-a-trip.jpg"}]
  )

(defn directory-info []
  [:div
   "Current archive: "
   (or (:directory-name @app-state)
       "None selected")])

(defn app []
  (let [dir-handle (:dir-handle @app-state)
        photos (:photos @app-state)
        selected-id (:selected-photo-id @app-state)
        selected-photo (when selected-id
                         (get-photo-by-id photos selected-id))]

    ;; (-> file-handle
    ;;     (.getFile)
    ;;     (.then
    ;;      (fn [file]
    ;;        (let [url (js/URL.createObjectURL file)]
    ;;          (reset! selected-image url)))))


    ;;    (if (empty? photos)
    ;; Select data directory
    ;; [folder-selector]
    [:div.app
     [:header.app-header
      [:h1 "Photos"]
      [:button.btn-small {:on-click load-photos-from-directory}
       "📁 Change Folder"]]

     ;; [gallery photos select-photo]
     ;; (photo-page)
     (directory-info)

     [:p "Photo"]
     [:hr]
     [:h4 "Debug"]
     (if (nil? dir-handle)
       [:p "dir-handle " "nil"]
       [:div
        [:p "dir-handle: " (.-name dir-handle) "/"]
        ]
       )
     (if (empty? photos)
       [:p "photos " "-none-"]
       [:p "photos " photos]
       )
     (if (empty? selected-id)
       [:p "selected-id " "-none-"]
       [:p "selected0-id " selected-id]
       )

     ;; [:p (string (.-name (:dir-handle app-state)))]

     ;; [:img {:src "file:data/test-images/duckling-on-a-trip.jpg"}]
     ;; [:img {:src @selected-image}]
     ;;(image-view)

     ;;     (when selected-photo
     ;;     [photo-viewer selected-photo close-viewer prev-photo next-photo])])
     ]
    ))

(rd/render [app]
(. js/document (getElementById "app")))

(defn on-js-reload []
;; Reload hook for development
)
