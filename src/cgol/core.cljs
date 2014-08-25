(ns cgol.core
  (:require [clojure.set :as s]
            [quil.core :as q :include-macros true]
            [quil.middleware :as qm]
            [cgol.game :as g]))

(enable-console-print!)

(def blinker #{[9 0] [9 1] [9 2]})

(def beacon #{[0 0] [1 0]
              [0 1] [1 1]
              [2 2] [3 2]
              [2 3] [3 3]})

(def glider #{[20 20] [21 21]
              [19 22] [20 22] [21 22]})

(def initial-state {:size [50 50]
                    :cells (s/union blinker beacon glider)})

(def cell-dimensions [10 10])
(def cell-color [0 200 0])

(defn coord-pixel [[row col] [cell-width cell-height]]
  [(* row cell-height) (* col cell-width)])

(defn pixel-coord [[x y] [cell-width cell-height]]
  [(quot x cell-height) (quot y cell-width)])

(defn draw-grid [{:keys [cells]} [cell-width cell-height]]
  (apply q/fill cell-color)
  (doseq [cell cells]
    (let [[x y] (coord-pixel cell cell-dimensions)]
      (q/quad
        x y
        x (+ y cell-width)
        (+ x cell-height) (+ y cell-width)
        (+ x cell-height) y))))

(defn setup []
  (q/smooth)
  (q/frame-rate 2)
  initial-state)

(defn update [state]
  (assoc state :cells (g/next-generation state)))

(defn add-live-cell [state {:keys [x y]}]
  (assoc state :cells (conj (:cells state)
                            (pixel-coord [x y] cell-dimensions))))

(defn draw [state]
  (q/background 255)
  (draw-grid state cell-dimensions))

(q/defsketch game
  :host "canvas"
  :middleware [qm/fun-mode]
  :setup setup
  :update update
  :draw draw
  :size (coord-pixel (:size initial-state) cell-dimensions))

