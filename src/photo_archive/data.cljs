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
  [{:filename "duckling-on-a-trip.jpg"
    }]
  )
