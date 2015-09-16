(ns space-invaders.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
    {:player {:x 50}
   :bullets []
   :frame 1})

(def player-speed 2)

(def bullet-speed 1)

(def fire-rate 10)

(defn fire-bullet [state]
  (assoc state
         :bullets (if (= 0 (mod (:frame state) fire-rate))
                    (cons {:x (-> state :player :x) :y 90}
                          (:bullets state))
                    (:bullets state))))

(defn update-bullets [bullets]
  (filter (fn [bullet] (> (:x bullet) 0 ))
          (map (fn [bullet]
                 {:x (:x bullet)
                  :y (- (:y bullet) bullet-speed)})
               bullets)))

(defn update-state [state]
  (assoc state
         :bullets (-> state
                       fire-bullet
                       :bullets
                       update-bullets)
         :frame (+ 1 (:frame state))))

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
      (q/ellipse start-x start-y size size))))

(defn draw-state [state]
  (q/background 240)
  (draw-player (q/width) (q/height) (-> state :player :x))
  (draw-bullets (q/width) (q/height) (:bullets state)))


(defn key-pressed [old-state event]
  (let [player-x (-> old-state :player :x)]
  (case  (:key-code event)
    37 (assoc old-state :player {:x (- player-x player-speed)})
    39 (assoc old-state :player {:x (+ player-x player-speed)})
    )))

(q/defsketch space-invaders
  :title "SPACE INVADERS IN SPACE"
  :size [500 500]
  :setup setup
  :key-pressed key-pressed
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
