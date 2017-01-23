(ns esmeralda.core
  (:import [java.awt Canvas GraphicsEnvironment BorderLayout]
           [javax.swing JFrame]))

(def width 800)
(def height 600)

(defn canvas
  [width height]
  (let [canvas (new java.awt.Canvas)]
    (.setSize canvas width height)
    canvas))

(defn display
  []
  (let [canvas (canvas width height)
        frame (new JFrame "FrameDemo")]
    (.add (.getContentPane frame) canvas)
    (doto frame
      ;; alternatively, JFrame/EXIT_ON_CLOSE will kill the process
      (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)
      (.pack)
      (.show))
    (.createBufferStrategy canvas 2)
    (let [strategy (.getBufferStrategy canvas)
          graphics (.getDrawGraphics strategy)]
      {:canvas canvas
       :strategy strategy
       :graphics graphics
       :frame frame})))

(def display-map
  (display))

(defn draw-dungeon-walls
  [{:keys [graphics strategy]}]
  (.setColor graphics java.awt.Color/BLACK)
  (.fillRect graphics 0 0 width height)
  (.show strategy)
  (.setColor graphics java.awt.Color/BLUE)
  (.drawRect graphics 10 10 (- width 20) (- height 20))
  (.show strategy))
