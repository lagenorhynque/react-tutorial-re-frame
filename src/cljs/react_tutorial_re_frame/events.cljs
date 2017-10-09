(ns react-tutorial-re-frame.events
  (:require [re-frame.core :as re-frame]
            [react-tutorial-re-frame.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

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

(re-frame/reg-event-db
 :handle-click
 (fn [db [_ i]]
   (let [{:keys [history x-is-next? step-number]} (:game-state db)
         history (vec (take (inc step-number) history))
         current (nth history (dec (count history)))
         squares (:squares current)]
     (if (or (calculate-winner squares)
             (squares i))
       db
       (update-in db [:game-state] assoc
                  :history (conj history
                                 {:squares (assoc
                                            squares
                                            i
                                            (if x-is-next? "X" "O"))
                                  :i i})
                  :x-is-next? (not x-is-next?)
                  :step-number (count history))))))

(re-frame/reg-event-db
 :jump-to
 (fn [db [_ step]]
   (update-in db [:game-state] assoc
              :x-is-next? (even? step)
              :step-number step)))

(re-frame/reg-event-db
 :flip-sort-order
 (fn [db _]
   (let [{:keys [order-asc?]} (:game-state db)]
     (update-in db [:game-state] assoc
                :order-asc? (not order-asc?)))))
