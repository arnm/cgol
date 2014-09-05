(ns cgol.state
  (:require [clojure.set :as s]))

(def glider #{[20 20] [21 21]
              [19 22] [20 22] [21 22]})

(def blinker #{[9 0] [9 1] [9 2]})

(def beacon #{[0 0] [1 0]
              [0 1] [1 1]
              [2 2] [3 2]
              [2 3] [3 3]})

(def glider #{[20 20] [21 21]
              [19 22] [20 22] [21 22]})

(def state (atom
            {:title "CGOL"
             :grid {:dimensions [50 50] ;rows x columns
                     :cells (s/union glider blinker beacon)           ;row x col
                     }
             :cell-size 10
             :cell-color "rgb(0, 255, 0)"}))
