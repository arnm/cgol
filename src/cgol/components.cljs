(ns cgol.components
  (:require [clojure.set :as s]
            [om.core :as om :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [sablono.core :refer-macros [html]]
            [cgol.state :refer [state]]
            [cgol.gol :as gol]))

(def mouse-down (atom false))
(def black "#000000")
(def white "#FFFFFF")
(def light-gray "#8C8C8C")

(defn coord-pixel [[row col] cell-size]
  [(* col cell-size) (* row cell-size)])

(defn pixel-coord [[x y] cell-size]
  [(quot y cell-size) (quot x cell-size)])

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

(defn mouse-position [canvas event]
  "doc"
  (let [rect (.getBoundingClientRect canvas)]
    [(- (.-clientX event) (.-left rect))
     (- (.-clientY event) (.-top rect))]))

(defn on-mouse-down! [event]
  "doc"
  (reset! mouse-down true))

(defn on-mouse-up! [event]
  "doc"
  (reset! mouse-down false))

(defn on-mouse-move! [canvas event cell-size]
  "doc"
  (if @mouse-down
    (let [[row col] (pixel-coord (mouse-position canvas event) cell-size)]
      (swap! state update-in [:grid :cells] #(s/union % #{[row col]})))))

(defcomponent canvas
  [app owner]
  (did-mount
   [_]
   (let [canvas (om/get-node owner "canvas")
         [col-count row-count] (get-in app [:grid :dimensions])
         cell-size (:cell-size app)]
     (.addEventListener canvas "mouseup" on-mouse-up! false)
     (.addEventListener canvas "mousedown" on-mouse-down! false)
     (.addEventListener canvas "mousemove" #(on-mouse-move! canvas % cell-size) false)

     (set! (.-width canvas) (* col-count cell-size))
     (set! (.-height canvas) (* row-count cell-size))
     (draw-canvas! app canvas)))
  (did-update
   [_ _ _]
   (draw-canvas! app (om/get-node owner "canvas")))
  (render
   [_]
   (html
    [:div
     [:canvas {:ref "canvas"}]])))

(defn update-cells []
  (swap! state update-in [:grid] gol/next-generation))

(defcomponent app
  [app owner]
  (did-mount
   [_]
   (js/setInterval update-cells 500))
  (render
   [_]
   (html
    [:div
     [:h1 (:title app)]
     [:h2 "Generation: " (get-in app [:grid :generation])]
     (om/build canvas app)])))
