(ns cgol.game)

(defn get-cell-neighbors
  "Returns set of cell neighbors, dead or alive"
  [{:keys [size]} [cell-row cell-col]]
  (let [start-row (- cell-row 1) start-col (- cell-col 1)
        end-row (+ cell-row 2) end-col (+ cell-col 2)
        [max-row max-col] size
        nested-cells (for [row (range start-row end-row)]
                       (for [col (range start-col end-col)]
                         (if (and
                               (not (= [row col] [cell-row cell-col]))
                               (and (<= row max-row) (>= row 0))
                               (and (<= col max-col) (>= col 0)))
                           [row col])))]
    (->> nested-cells
         flatten (filter #(not (nil? %)))
         (partition 2) (map vec) set)))

(defn is-cell-alive?
  "Returns true if, for the grid, the cell is alive"
  [{:keys [cells]} cell]
  (contains? cells cell))

(defn get-cell-live-neighbors
  "Returns living neighbors of specified cell"
  [grid cell]
  (filter #(is-cell-alive? grid %) (get-cell-neighbors grid cell)))

(defn get-next-generation
  "Returns living cells based on the past living cells"
  [grid]
  (let [nested-cells (for [cell (:cells grid)]
                       (let [neighbors-count
                             (count (get-cell-live-neighbors grid cell))]
                         (if (or (= neighbors-count 2)
                                 (= neighbors-count 3))
                           cell)))]
    (filter #(not (nil? %)) nested-cells)))
