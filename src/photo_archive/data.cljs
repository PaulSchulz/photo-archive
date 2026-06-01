(ns photo-archive.data)

;; SVG placeholder generator
(defn make-svg-placeholder [color text width height]
  (str "data:image/svg+xml;utf8,"
       "<svg xmlns='http://www.w3.org/2000/svg' width='" width "' height='" height "'>"
       "<rect fill='" color "' width='" width "' height='" height "'/>"
       "<text x='50%' y='50%' font-size='14' fill='white' text-anchor='middle' dy='.3em'>"
       text "</text>"
       "</svg>"))

;; Sample photo data - in production this would come from a backend/API
(def sample-photos
  [{:id 1
    :title "Sunset at the Beach"
    :filename "sunset-beach.jpg"
    :url (make-svg-placeholder "#ff6b35" "Sunset Beach" 300 200)
    :thumbnail (make-svg-placeholder "#ff6b35" "Sunset" 150 100)
    :metadata {:date "2026-01-15"
               :camera "Canon EOS 5D"
               :iso 400
               :aperture "f/2.8"
               :shutter-speed "1/250s"
               :focal-length "50mm"
               :location "Bondi Beach, Sydney"
               :description "Beautiful golden hour lighting"}}
   {:id 2
    :title "Mountain Vista"
    :filename "mountain.jpg"
    :url (make-svg-placeholder "#004e89" "Mountain Vista" 300 200)
    :thumbnail (make-svg-placeholder "#004e89" "Mountain" 150 100)
    :metadata {:date "2026-02-03"
               :camera "Sony A7III"
               :iso 100
               :aperture "f/5.6"
               :shutter-speed "1/500s"
               :focal-length "35mm"
               :location "Blue Mountains, NSW"
               :description "Misty mountain landscape"}}
   {:id 3
    :title "City Lights"
    :filename "city-lights.jpg"
    :url (make-svg-placeholder "#1b1e23" "City Lights" 300 200)
    :thumbnail (make-svg-placeholder "#1b1e23" "City" 150 100)
    :metadata {:date "2026-01-28"
               :camera "Nikon Z6"
               :iso 3200
               :aperture "f/4"
               :shutter-speed "1/125s"
               :focal-length "24mm"
               :location "Sydney CBD"
               :description "Night cityscape"}}
   {:id 4
    :title "Forest Path"
    :filename "forest.jpg"
    :url (make-svg-placeholder "#2d6a4f" "Forest Path" 300 200)
    :thumbnail (make-svg-placeholder "#2d6a4f" "Forest" 150 100)
    :metadata {:date "2026-03-10"
               :camera "Canon EOS 5D"
               :iso 200
               :aperture "f/4.5"
               :shutter-speed "1/250s"
               :focal-length "85mm"
               :location "Dandenong Ranges"
               :description "Misty forest walkway"}}
   {:id 5
    :title "Ocean Waves"
    :filename "waves.jpg"
    :url (make-svg-placeholder "#0077b6" "Ocean Waves" 300 200)
    :thumbnail (make-svg-placeholder "#0077b6" "Waves" 150 100)
    :metadata {:date "2026-02-14"
               :camera "GoPro Hero 10"
               :iso 100
               :aperture "f/2.8"
               :shutter-speed "1/1000s"
               :focal-length "17mm"
               :location "Cronulla Beach"
               :description "Powerful ocean swells"}}
   {:id 6
    :title "Autumn Leaves"
    :filename "autumn.jpg"
    :url (make-svg-placeholder "#d4a574" "Autumn Leaves" 300 200)
    :thumbnail (make-svg-placeholder "#d4a574" "Autumn" 150 100)
    :metadata {:date "2026-03-25"
               :camera "Fujifilm X-T4"
               :iso 400
               :aperture "f/2"
               :shutter-speed "1/500s"
               :focal-length "56mm"
               :location "Carrington Falls"
               :description "Golden autumn foliage"}}])
