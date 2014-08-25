(ns cgol.core
  (:require [quil.core :as q :include-macros true]
            [cgol.game :as g]))

(enable-console-print!)

(def simulate (atom false))
(def grid (atom {:size [50 50]
                 :cells #{[0 0] [1 0] [1 1] [2 0]}})) ; represents current living cells

(def cell-width 10)
(def cell-height 10)
(def cell-color [0 200 0])

(defn coord-pixel [[row col]]
  [(* row cell-height) (* col cell-width)])

(defn pixel-coord [[x y]]
  [(quot x cell-height) (quot y cell-width)])

(defn update! []
  (println @grid)
  (swap! grid assoc :cells (g/get-next-generation @grid)))

(defn draw-grid [{:keys [cells]}]
  (doseq [cell cells]
    (let [[x y] (coord-pixel cell)]
      (q/quad
        x y
        x (+ y cell-width)
        (+ x cell-height) (+ y cell-width)
        (+ x cell-height) y))))

(defn add-live-cell
  [grid cell]
  (assoc grid :cells (conj (:cells grid) cell)))

(defn mouse-clicked []
  (swap! grid add-live-cell (pixel-coord [(q/mouse-x) (q/mouse-y)])))

(defn setup []
  (q/smooth)
  (q/frame-rate 1)
  (q/background 255))

(defn draw []
  (apply q/fill cell-color)
  (draw-grid @grid)
  (update!))

(q/defsketch game
  :host "canvas"
  :setup setup
  :draw draw
  :size (coord-pixel (:size @grid))
  :mouse-clicked mouse-clicked)

