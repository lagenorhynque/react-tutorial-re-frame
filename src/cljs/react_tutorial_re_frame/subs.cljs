(ns react-tutorial-re-frame.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :game-state
 (fn [db _]
   (:game-state db)))

(re-frame/reg-sub
 :history
 :<- [:game-state]
 (fn [game-state _]
   (:history game-state)))

(re-frame/reg-sub
 :step-number
 :<- [:game-state]
 (fn [game-state _]
   (:step-number game-state)))

(re-frame/reg-sub
 :current
 :<- [:history]
 :<- [:step-number]
 (fn [[history step-number] _]
   (nth history step-number)))

(re-frame/reg-sub
 :current-squares
 :<- [:current]
 (fn [current _]
   (:squares current)))

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

(re-frame/reg-sub
 :winner
 :<- [:current-squares]
 (fn [current-squares _]
   (calculate-winner current-squares)))
