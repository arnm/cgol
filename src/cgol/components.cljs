(ns cgol.components
  (:require [om.core :as om :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [sablono.core :refer-macros [html]]
            [cgol.state :refer [state]]
            [cgol.gol :as gol]))

(def black "#000000")
(def white "#FFFFFF")
(def light-gray "#8C8C8C")

(defn coord-pixel [[row col] cell-size]
  [(* col cell-size) (* row cell-size)])

(defn pixel-coord [[x y] [cell-height cell-width]]
  [(quot y cell-height) (quot x cell-width)])

(defn draw-cells!
  [ctx cells cell-size]
  (doseq [cell cells]
      (let [[x y] (coord-pixel cell cell-size)]
        (.. ctx (fillRect x y cell-size cell-size))
        (.. ctx (strokeRect x y cell-size cell-size)))))

(defn draw-grid!
  [ctx [row-count col-count] cell-size]
  (doseq [row (range row-count)]
    (doseq [col (range col-count)]
      (let [[x y] (coord-pixel [row col] cell-size)]
        (.. ctx (fillRect x y cell-size cell-size))
        (.. ctx (strokeRect x y cell-size cell-size))))))

(defn draw-canvas!
  [app canvas]
  (let [ctx (.getContext canvas "2d")
        cells (get-in app [:grid :cells])
        board-dimensions (get-in app [:grid :dimensions])
        cell-size (:cell-size app)]
    (set! (.-fillStyle ctx) white)
    (set! (.-strokeStyle ctx) light-gray)
    (draw-grid! ctx board-dimensions cell-size)
    (set! (.-fillStyle ctx) (:cell-color app))
    (set! (.-strokeStyle ctx) black)
    (draw-cells! ctx cells cell-size)))

(defcomponent canvas
  [app owner]
  (did-mount
   [_]
   (let [canvas (om/get-node owner "canvas")
         [col-count row-count] (get-in app [:grid :dimensions])
         cell-size (:cell-size app)]
     (set! (.. canvas -width) (* col-count cell-size))
     (set! (.. canvas -height) (* row-count cell-size))
     (draw-canvas! app canvas)))
  (did-update
   [_ _ _]
   (draw-canvas! app (om/get-node owner "canvas")))
  (render
   [_]
    ;; (swap! state update-in [:grid] gol/next-generation)
   (html
    [:div
     [:canvas {:ref "canvas"}]])))

(defcomponent app
  [app owner]
  (render
   [_]
   (html
    [:div
     [:h1 (:title app)]
     (om/build canvas app)])))
