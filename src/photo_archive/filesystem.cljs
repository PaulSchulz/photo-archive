(ns photo-archive.filesystem)

;; File System Access API wrapper
(def file-system-available? (some? (.-storage js/navigator)))

(defn request-directory-handle []
  "Request user permission to access a directory"
  (js/console.log "Opening directory picker")
  (if file-system-available?
    (js/showDirectoryPicker)
    (js/Promise.reject "File System Access API not available")))

(defn get-file-handle [dir-handle filename]
  "Get a file handle from a directory"
  (.getFileHandle dir-handle filename))

(defn read-file [file-handle]
  "Read file contents as text"
  (let [promise (js/Promise.
                  (fn [resolve reject]
                    (-> file-handle
                        (.getFile)
                        (.then (fn [file]
                                 (-> file
                                     (.text)
                                     (.then resolve)
                                     (.catch reject))))
                        (.catch reject))))]
    promise))

(defn read-file-as-data-url [file-handle]
  "Read file as data URL for images"
  (js/Promise.
    (fn [resolve reject]
      (-> file-handle
          (.getFile)
          (.then (fn [file]
                   (let [reader (js/FileReader.)]
                     (set! (.-onload reader)
                       (fn [] (resolve (.-result reader))))
                     (set! (.-onerror reader)
                       (fn [] (reject (.-error reader))))
                     (.readAsDataURL reader file))))
          (.catch reject)))))

(defn write-file [dir-handle filename content]
  "Write content to a file"
  (js/Promise.
    (fn [resolve reject]
      (-> dir-handle
          (.getFileHandle filename #js{:create true})
          (.then (fn [file-handle]
                   (-> file-handle
                       (.createWritable)
                       (.then (fn [writable]
                                (-> writable
                                    (.write content)
                                    (.then (fn []
                                             (-> writable
                                                 (.close)
                                                 (.then (fn [] (resolve true)))
                                                 (.catch reject))))
                                    (.catch reject))))
                       (.catch reject))))
          (.catch reject)))))

(defn scan-images [dir-handle]
  "Scan directory for image files"
  (js/Promise.
   (fn [resolve reject]
     (try
       (let [images (js/Array.)
             process-entries
             (fn process-entries [iterator]
               (-> iterator
                   (.next)
                   (.then
                    (fn [result]
                      (if (.-done result)
                        (resolve images)
                        (let [[name file-handle] (.-value result)]
                          (-> file-handle
                              (.getFile)
                              (.then
                               (fn [file]
                                 (let [type (.-type file)]
                                   (when (.startsWith type "image/")
                                     (.push images #js{:name name
                                                       :handle file-handle
                                                       :size (.-size file)
                                                       :type type
                                                       :lastModified (.-lastModified file)}))
                                   (process-entries iterator))))
                              (.catch reject)))))
                    (.catch reject))))]
         (process-entries (.entries dir-handle)))
       (catch js/Error e
         (reject e))))))
