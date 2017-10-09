(ns react-tutorial-re-frame.views
  (:require [clojure.string :as str]
            [re-frame.core :as re-frame]))

(defn square [& {:keys [value on-click win?]}]
  [:button.square {:on-click on-click
                   :class (when win?
                            "win-square")}
   value])

(defn calculate-winner [squares]
  (let [lines [[0 1 2]
               [3 4 5]
               [6 7 8]
               [0 3 6]
               [1 4 7]
               [2 5 8]
               [0 4 8]
               [2 4 6]]]
    (reduce (fn [_ [a b c :as win-ps]]
              (when (and (squares a)
                         (= (squares a) (squares b))
                         (= (squares a) (squares c)))
                (reduced [(squares a) win-ps])))
            nil
            lines)))

(defn board [& {:keys [squares win-ps]}]
  (letfn [(render-square [i]
            [square
             :value (squares i)
             :on-click #(re-frame/dispatch [:handle-click i])
             :win? (some #(= i %) win-ps)])]
    (into [:div]
          (->> (range 9)
               (map #(render-square %))
               (partition 3)
               (map #(into [:div.board-row] %))))))

(defn status [& {:keys [winner x-is-next?]}]
  [:div
   (if winner
     (str "Winner: " winner)
     (str "Next player: "
          (if x-is-next? "X" "O")))])

(defn sort-link [& {:keys [order-asc?]}]
  [:div
   [:a {:href "#"
        :on-click #(re-frame/dispatch [:flip-sort-order])}
    (if order-asc? "↓" "↑")]])

(defn moves [& {:keys [history step-number order-asc?]}]
  (letfn [(calculate-location [i]
            (map (comp inc Math/floor) [(/ i 3) (mod i 3)]))
          (move-info [move i]
                     (if (zero? move)
                       "Game start"
                       (str "Move #("
                            (str/join
                             ", "
                             (calculate-location i))
                            ")")))]
    [:ol (cond->> (map-indexed (fn [move {:keys [i]}]
                                 [:li {:key move
                                       :class (when (= move step-number)
                                                "move-selected")}
                                  [:a {:href "#"
                                       :on-click #(re-frame/dispatch [:jump-to move])}
                                   (move-info move i)]])
                               history)
           (not order-asc?) reverse)]))

(defn game []
  (let [state (re-frame/subscribe [:game-state])]
    (fn []
      (let [{:keys [history x-is-next? step-number order-asc?]} @state
            current (nth history step-number)
            [winner win-ps] (calculate-winner (:squares current))]
        [:div.game
         [:div.game-board
          [board
           :squares (:squares current)
           :win-ps win-ps]]
         [:div.game-info
          [status
           :winner winner
           :x-is-next? x-is-next?]
          [sort-link
           :order-asc? order-asc?]
          [moves
           :history history
           :step-number step-number
           :order-asc? order-asc?]]]))))

(defn main-panel []
  [game])
