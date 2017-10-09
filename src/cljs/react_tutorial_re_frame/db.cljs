(ns react-tutorial-re-frame.db)

(def default-db
  {:game-state {:history [{:squares (vec (repeat 9 nil))
                           :i nil}]
                :x-is-next? true
                :step-number 0
                :order-asc? true}})
