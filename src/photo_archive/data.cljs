(ns photo-archive.data)

;; Sample photo data - in production this would come from a backend/API
(def sample-photos
  [{:id 1
    :title "Sunset at the Beach"
    :filename "sunset-beach.jpg"
    :url "https://via.placeholder.com/300x200?text=Sunset"
    :thumbnail "https://via.placeholder.com/150x100?text=Sunset"
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
    :url "https://via.placeholder.com/300x200?text=Mountain"
    :thumbnail "https://via.placeholder.com/150x100?text=Mountain"
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
    :url "https://via.placeholder.com/300x200?text=City"
    :thumbnail "https://via.placeholder.com/150x100?text=City"
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
    :url "https://via.placeholder.com/300x200?text=Forest"
    :thumbnail "https://via.placeholder.com/150x100?text=Forest"
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
    :url "https://via.placeholder.com/300x200?text=Waves"
    :thumbnail "https://via.placeholder.com/150x100?text=Waves"
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
    :url "https://via.placeholder.com/300x200?text=Autumn"
    :thumbnail "https://via.placeholder.com/150x100?text=Autumn"
    :metadata {:date "2026-03-25"
               :camera "Fujifilm X-T4"
               :iso 400
               :aperture "f/2"
               :shutter-speed "1/500s"
               :focal-length "56mm"
               :location "Carrington Falls"
               :description "Golden autumn foliage"}}])
