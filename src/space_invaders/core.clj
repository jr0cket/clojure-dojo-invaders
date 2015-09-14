(ns space-invaders.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:player {:x 50}
   :bullets []
   :frame 1})

(defn update-bullets [bullets]
  (filter (fn [bullet] (> (:x bullet) 0 ))
          (map (fn [bullet]
                 {:x (:x bullet)
                  :y (- (:y bullet) 1)})
               bullets)))

(defn fire-bullet [state]
  {:player (:player state)
   :bullets (if (= 2 (mod (:frame state)))
              (cons {:x (:x (:player state)) :y 90}
                    (:bullets state))
              (:bullets state)
              )
   :frame (:frame state)})

(defn update-state [state]
  {:player (:player state)
   :bullets (update-bullets (:bullets (fire-bullet state)))
   :frame (+ 1(:frame state))}
              )

(defn draw-player [screen-width screen-height player-x]
  (let [width 30
        height 30
        x (* screen-width (/ player-x 100))
        y (/ screen-height 11/10)
        start-x (- x (/ width 2))]
    (q/rect start-x y width height)))

(defn draw-bullets [screen-width screen-height bullets]
  (doseq [bullet bullets]
    (let [size 5
          x (* screen-width (/ (:x bullet) 100))
          y (* screen-width (/ (:y bullet) 100))
          start-x (- x (/ size 2))
          start-y (- y (/ size 2))]
      (q/ellipse start-x start-y size size))
    )
  )

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  (draw-player (q/width) (q/height) (:x (:player state)))
  (draw-bullets (q/width) (q/height) (:bullets state)))


(defn key-pressed [old-state event]
  (case  (:key-code event)
    37 {:player {:x (- (:x (:player old-state)) 1)} :bullets (:bullets old-state) :frame (:frame old-state)}
    39 {:player {:x (+ (:x (:player old-state)) 1)} :bullets (:bullets old-state) :frame (:frame old-state)}))

(q/defsketch space-invaders
  :title "You spin my circle right round"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :key-pressed key-pressed
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
