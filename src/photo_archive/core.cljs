;;;; Namespace ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns photo-archive.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [promesa.core :as p]
   [clojure.string :as str]))

;;;; App State ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defonce app-state
  (r/atom
   {:dir-handle nil
    :loading false
    :error nil
    :photos []
    :current nil
    :current-url nil}))

;;;; Pick Directory ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn choose-directory! []
  (-> (js/window.showDirectoryPicker)
      (.then
       (fn [dir]
         (swap! app-state assoc :dir-handle dir)))
      (.catch
       (fn [e]
         (swap! app-state assoc :error e)))))

;;;; Image Helpers ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def image-exts #{"jpg" "jpeg" "png" "gif" "webp"})

(defn image-file? [name]
  (let [ext (some-> name (str/split #"\.") last str/lower-case)]
    (contains? image-exts ext)))

;;;; Scan Directories ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn scan-directory! []
  (let [dir (:dir-handle @app-state)]
    (when dir
      (swap! app-state assoc :loading true :photos [])

      (p/let [entries (js/Array.from (.entries dir))]

        (let [photos
              (->> entries
                   (map (fn [[name handle]]
                          (when (and (= "file" (.-kind handle))
                                     (image-file? name))
                            {:name name
                             :handle handle})))
                   (remove nil?)
                   vec)]

          (swap! app-state assoc
                 :photos photos
                 :loading false))))))

;;;; Load Image URL ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn show-image! [photo]
  (let [old-url (:current-url @app-state)]
    (when old-url
      (js/URL.revokeObjectURL old-url)))

  (p/let [file (.getFile (:handle photo))
          url  (js/URL.createObjectURL file)]

    (swap! app-state assoc
           :current photo
           :current-url url)))

;;;; UI Components ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Controlls
(defn controls []
  [:div
   [:button {:on-click choose-directory!}
    "Open Directory"]

   [:button {:on-click scan-directory!}
    "Scan Images"]])

;;;; Photo List
(defn photo-list []
  (let [photos (:photos @app-state)]
    [:ul
     (for [p photos]
       ^{:key (:name p)}
       [:li
        [:a {:href "#"
             :on-click #(show-image! p)}
         (:name p)]])]))

;;;; Image Viewer
(defn image-view []
  (let [url (:current-url @app-state)]
    (when url
      [:img
       {:src url
        :style {:max-width "100%"
                :max-height "70vh"
                :display "block"
                :margin-top "1em"}}])))

;;;; Status
(defn status []
  (let [{:keys [loading error dir-handle]} @app-state]
    [:div
     [:div "Directory: "
      (or (some-> dir-handle .-name) "None")]

     (when loading
       [:div "Loading..."])

     (when error
       [:div {:style {:color "red"}}
        (str error)])]))

;;;; Main Page ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn app []
  [:div
   {:style {:font-family "sans-serif"
            :padding "1em"}}

   [:h1 "Photo Archive"]

   [controls]
   [status]

   [:h2 "Photos"]
   [photo-list]

   [:h2 "Viewer"]
   [image-view]])

;;;; Mount ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn mount! []
  (println "Mounting app...")
  (rdom/render
   [app]
   (.getElementById js/document "app")))

(defn ^:export init []
  (mount!))
