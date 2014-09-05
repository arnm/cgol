(ns cgol.core
  (:require [om.core :as om :include-macros true]
            [cgol.components :as com]
            [cgol.state :refer [state]]))

(enable-console-print!)

(om/root com/app state
         {:target (.getElementById js/document "app")})

