(ns esmeralda.core
  (:import [java.awt Canvas GraphicsEnvironment BorderLayout]
           [javax.swing JFrame]
           [java.awt.event KeyListener KeyEvent]))

(def screen-width 800)
(def screen-height 600)

(def dungeon-width (- screen-width 20))
(def dungeon-height (- screen-height 20))

(def game-over? false)

(def key-map {87 :w
              65 :a
              83 :s
              68 :d
              38 :up-arrow
              37 :left-arrow
              40 :down-arrow
              39 :right-arrow
              32 :space-key
              8 :del-key
              10 :return})

(def key-state (atom
                (apply hash-map (interleave (vals key-map) (repeat false)))))

(defn key-listener
  []
  (proxy [KeyListener] []
    (keyPressed [^KeyEvent e]
      (swap! key-state assoc (get key-map (.getKeyCode e)) true))
    (keyReleased [^KeyEvent e]
      (swap! key-state assoc (get key-map (.getKeyCode e)) false))))

(defn canvas
  [screen-width screen-height]
  (let [canvas (new java.awt.Canvas)]
    (.setSize canvas screen-width screen-height)
    (.addKeyListener canvas (key-listener))
    canvas))

(defn display
  []
  (let [canvas (canvas screen-width screen-height)
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

;; https://docs.oracle.com/javase/7/docs/api/java/awt/Graphics.html
;; for drawing methods

(defn draw-dungeon-walls
  [display-map]
  (let [{:keys [graphics strategy]} display-map]
    (.setColor graphics java.awt.Color/BLACK)
    (.fillRect graphics 0 0 screen-width screen-height)
    ;;(.show strategy)
    (.setColor graphics java.awt.Color/BLUE)
    (.drawRect graphics 10 10 dungeon-width dungeon-height)
    ;;(.show strategy)
    display-map))

(def hero
  (let [oval-width 100
        oval-height 100]
    (atom {:oval-width oval-width
           :oval-height oval-height
           :x (- (/ screen-width 2) (/ oval-width 2))
           :y (- (- screen-height 100) (/ oval-height 2))
           })))

(defn draw-hero
  [display-map]
  (let [{:keys [graphics strategy]} display-map]
    (.setColor graphics java.awt.Color/RED)
    (.drawOval graphics
               (:x @hero)
               (:y @hero)
               (:oval-width @hero)
               (:oval-height @hero))
    ;;(.show strategy)
    display-map))

(defn update-hero!
  "Given hero and key-state atoms, update hero"
  [hero key-state]
  (let [delta-d 1]
    (when (:w @key-state)
      (swap! hero assoc :y (- (:y @hero) delta-d)))
    (when (:s @key-state)
      (swap! hero assoc :y (+ (:y @hero) delta-d)))
    (when (:d @key-state)
      (swap! hero assoc :x (+ (:x @hero) delta-d)))
    (when (:a @key-state)
      (swap! hero assoc :x (- (:x @hero) delta-d)))))

(defn game-loop
  [current-time previous-time]
  ;; update positions
  (update-hero! hero key-state)
  ;; draw to the buffer
  (-> display-map
      (draw-dungeon-walls)
      (draw-hero))
  ;; refresh the display
  ;;(.dispose (:graphics display-map))
  (.show (:strategy display-map))
  ;; refresh the display
  ;;(.dispose (:graphics display-map))
  )

(defn main-loop
  []
  (loop [current-time (System/currentTimeMillis)
         previous-time nil]
    (game-loop current-time previous-time)
    (if (not game-over?)
      (recur (System/currentTimeMillis) current-time))))
