(ns photo-archive.filesystem
  (:require [cljs.core.async :refer [go chan put! <! >!]]
            [cljs.core.async.interop :refer-macros [<p!]]))

;; File System Access API wrapper
(def file-system-available? (some? (.-storage js/navigator)))

(defn request-directory-handle []
  "Request user permission to access a directory"
  (if file-system-available?
    (js/showDirectoryPicker)
    (js/Promise.reject "File System Access API not available")))

(defn get-file-handle [dir-handle filename]
  "Get a file handle from a directory"
  (.getFileHandle dir-handle filename))

(defn list-directory [dir-handle]
  "List all files in a directory"
  (go
    (try
      (let [entries (js/Array.)]
        (js/for (entry (js/await (.entries dir-handle)))
          (.push entries entry))
        entries)
      (catch :default e
        (js/console.error "Error listing directory:" e)
        nil))))

(defn read-file [file-handle]
  "Read file contents"
  (go
    (try
      (let [file (<p! (.getFile file-handle))
            text (<p! (.text file))]
        text)
      (catch :default e
        (js/console.error "Error reading file:" e)
        nil))))

(defn read-file-as-data-url [file-handle]
  "Read file as data URL for images"
  (go
    (try
      (let [file (<p! (.getFile file-handle))
            reader (js/FileReader.)]
        (js/Promise.
          (fn [resolve reject]
            (set! (.-onload reader)
              (fn [] (resolve (.-result reader))))
            (set! (.-onerror reader)
              (fn [] (reject (.-error reader))))
            (.readAsDataURL reader file)))
        )
      (catch :default e
        (js/console.error "Error reading file as data URL:" e)
        nil))))

(defn write-file [dir-handle filename content]
  "Write content to a file"
  (go
    (try
      (let [file-handle (<p! (.getFileHandle dir-handle filename #js{:create true}))
            writable (<p! (.createWritable file-handle))]
        (<p! (.write writable content))
        (<p! (.close writable))
        true)
      (catch :default e
        (js/console.error "Error writing file:" e)
        false))))

(defn scan-images [dir-handle]
  "Scan directory for image files"
  (go
    (try
      (let [images (js/Array.)]
        (js/for-await [entry (js/await (.entries dir-handle))]
          (let [[name file-handle] entry
                file (<p! (.getFile file-handle))
                type (.-type file)]
            (when (.startsWith type "image/")
              (.push images #js{:name name
                               :handle file-handle
                               :size (.-size file)
                               :type type
                               :lastModified (.-lastModified file)}))))
        images)
      (catch :default e
        (js/console.error "Error scanning images:" e)
        nil))))
