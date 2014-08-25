(ns cgol.game
  (:require [clojure.set :as s]))

(defn neighbors
  "Returns set of cell neighbors, both dead and alive"
  [{:keys [size]} [cell-row cell-col]]
  (let [start-row (- cell-row 1) start-col (- cell-col 1)
        end-row (+ cell-row 2) end-col (+ cell-col 2)
        [max-row max-col] size
        nested-cells (for [row (range start-row end-row)]
                       (for [col (range start-col end-col)]
                         (if (and (not (= [row col] [cell-row cell-col]))
                                  (and (<= row max-row) (>= row 0))
                                  (and (<= col max-col) (>= col 0)))
                           [row col])))]
    (->> nested-cells
         flatten (filter #(not (nil? %)))
         (partition 2) (map vec) set)))

(defn alive?
  "Returns true if, for the grid, the cell is alive"
  [{:keys [cells]} cell]
  (contains? cells cell))

(defn live-neighbors
  "Returns living neighbors of specified cell"
  [grid cell]
  (filter #(alive? grid %) (neighbors grid cell)))

(defn dead-neighbors
  "Returns dead neighbors of specified cell"
  [grid cell]
  (filter #(not (alive? grid %)) (neighbors grid cell)))

(defn survive?
  "Returns true if living cell survives for next generation"
  [grid cell]
  (let [live-neighbor-count (count (live-neighbors grid cell))]
    (if (or (= live-neighbor-count 2)
            (= live-neighbor-count 3))
      true false)))

(defn awaken?
  "Returns true if cell awakens for next generation"
  [grid cell]
  (let [live-neighbors-count (count (live-neighbors grid cell))]
    (if (= live-neighbors-count 3)
      true false)))

(defn next-generation
  "Returns living cells based on the past living cells"
  [{:keys [cells] :as grid}]
  (let [living-cells-dead-neighbors
        (->> (map #(dead-neighbors grid %) cells)
             flatten (partition 2) (map vec) set)
        surviving-cells (set (filter #(survive? grid %) cells))
        awakening-cells (set (filter #(awaken? grid %)
                                living-cells-dead-neighbors))]
    (s/union surviving-cells awakening-cells)))
