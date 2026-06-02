(ns photo-archive.manifest
  (:require [photo-archive.filesystem :as fs]
            [cljs.core.async :refer [go chan put! <! >!]]
            [cljs.core.async.interop :refer-macros [<p!]]
            [cljs.reader :as reader]
            [goog.json :as json]))

(defn parse-json [json-str]
  "Parse JSON string to ClojureScript data structure"
  (try
    (js->clj (JSON.parse json-str) :keywordize-keys true)
    (catch :default e
      (js/console.error "Error parsing JSON:" e)
      nil)))

(defn stringify-json [data]
  "Convert ClojureScript data structure to JSON string"
  (.stringify JSON (clj->js data) nil 2))

(defn load-or-create-manifest [dir-handle images]
  "Load manifest.json if exists, otherwise create new one"
  (go
    (try
      (let [manifest-file (<p! (fs/get-file-handle dir-handle "manifest.json"))]
        (try
          (let [manifest-text (<p! (fs/read-file manifest-file))]
            (parse-json manifest-text))
          (catch :default e
            ;; File exists but couldn't read, create new
            (create-manifest images dir-handle))))
      (catch :default e
        ;; File doesn't exist, create new
        (create-manifest images dir-handle)))))

(defn create-manifest [images dir-handle]
  "Create a new manifest from discovered images"
  (go
    (try
      (let [photos (for [img images]
                     {:id (random-uuid)
                      :filename (.-name img)
                      :title (.-name img)
                      :file-size (.-size img)
                      :file-type (.-type img)
                      :last-modified (.-lastModified img)
                      :metadata {:date ""
                                 :camera ""
                                 :iso ""
                                 :aperture ""
                                 :shutter-speed ""
                                 :focal-length ""
                                 :location ""
                                 :description ""}})
            manifest {:version "1.0"
                      :created (js/Date.)
                      :photos (vec photos)}
            manifest-json (stringify-json manifest)]
        ;; Save manifest
        (<p! (fs/write-file dir-handle "manifest.json" manifest-json))
        manifest)
      (catch :default e
        (js/console.error "Error creating manifest:" e)
        nil))))

(defn save-manifest [dir-handle manifest]
  "Save manifest to file"
  (go
    (try
      (let [manifest-json (stringify-json manifest)]
        (<p! (fs/write-file dir-handle "manifest.json" manifest-json))
        true)
      (catch :default e
        (js/console.error "Error saving manifest:" e)
        false))))

(defn load-photos-with-data-urls [manifest file-handles]
  "Load photo data URLs from file handles"
  (go
    (try
      (let [photos-with-urls (for [photo (:photos manifest)
                                    :let [handle (get file-handles (:filename photo))]]
                               (if handle
                                 (let [data-url (<p! (fs/read-file-as-data-url handle))]
                                   (assoc photo :url data-url))
                                 photo))]
        (vec photos-with-urls))
      (catch :default e
        (js/console.error "Error loading photo URLs:" e)
        (:photos manifest)))))
