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

(defonce display-map
  (display))

;; https://docs.oracle.com/javase/7/docs/api/java/awt/Graphics.html
;; for drawing methods

(defn draw-dungeon-walls
  [display-map]
  (let [{:keys [graphics strategy]} display-map]
    (.setColor graphics java.awt.Color/BLACK)
    (.fillRect graphics 0 0 width height)
    (.show strategy)
    (.setColor graphics java.awt.Color/BLUE)
    (.drawRect graphics 10 10 (- width 20) (- height 20))
    (.show strategy)
    display-map))

(defn draw-hero
  [display-map]
  (let [{:keys [graphics strategy]} display-map
        oval-width 100
        oval-height 100]
    (.setColor graphics java.awt.Color/RED)
    (.drawOval graphics
               (- (/ width 2) (/ oval-width 2))
               (- (- height 100) (/ oval-height 2))
               oval-width
               oval-height)
    (.show strategy)
    display-map))
