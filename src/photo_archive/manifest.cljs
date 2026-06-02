(ns photo-archive.manifest)

;; JSON utilities
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

(defn uuid []
  "Generate a UUID-like string"
  (let [s (.. js/Math (random) (toString 36) (substr 2 9))]
    s))

;; Manifest management
(defn load-or-create-manifest [dir-handle images]
  "Load manifest.json if exists, otherwise create new one"
  (js/Promise.
    (fn [resolve reject]
      (-> dir-handle
          (.getFileHandle "manifest.json")
          (.then (fn [manifest-file]
                   (-> manifest-file
                       (.getFile)
                       (.then (fn [file]
                                (-> file
                                    (.text)
                                    (.then (fn [text]
                                             (resolve (parse-json text))))
                                    (.catch (fn [] 
                                              ;; File exists but can't read, create new
                                              (resolve (create-manifest images)))))))
                       (.catch reject))))
          (.catch (fn [e]
                    ;; File doesn't exist, create new
                    (resolve (create-manifest images))))))))

(defn create-manifest [images]
  "Create a new manifest from discovered images"
  (let [photos (for [img images]
                 {:id (uuid)
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
                  :created (.toISOString (js/Date.))
                  :photos (vec photos)}]
    manifest))

(defn save-manifest [dir-handle manifest]
  "Save manifest to file"
  (let [manifest-json (stringify-json manifest)]
    (-> dir-handle
        (.getFileHandle "manifest.json" #js{:create true})
        (.then (fn [file-handle]
                 (-> file-handle
                     (.createWritable)
                     (.then (fn [writable]
                              (-> writable
                                  (.write manifest-json)
                                  (.then (fn []
                                           (-> writable
                                               (.close)
                                               (.then (fn [] true)))))))))))))

(defn load-photos-with-data-urls [manifest file-handles]
  "Load photo data URLs from file handles"
  (js/Promise.all
    (map (fn [photo]
           (let [handle (get file-handles (:filename photo))]
             (if handle
               (-> handle
                   (.getFile)
                   (.then (fn [file]
                            (let [reader (js/FileReader.)]
                              (js/Promise.
                                (fn [resolve reject]
                                  (set! (.-onload reader)
                                    (fn []
                                      (resolve (assoc photo :url (.-result reader)))))
                                  (set! (.-onerror reader)
                                    (fn []
                                      (reject (.-error reader))))
                                  (.readAsDataURL reader file)))))))
               (js/Promise.resolve photo))))
         (:photos manifest)))))
