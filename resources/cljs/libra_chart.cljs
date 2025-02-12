(ns libra-chart
  (:require
   ["chart.js" :as chart]
   ["chartjs-adapter-date-fns"]
   ["chartjs-plugin-zoom$default" :as zoom]
   ["chartjs-plugin-annotation$default" :as annotations]
   ["date-fns" :as date-fns]
   ["mobx" :as mobx]))

(apply (.-register chart/Chart) (into [] (concat chart/registerables [zoom annotations])))

(def store
  (mobx/makeAutoObservable
   (atom
    {:data []})))

(def myChart (chart/Chart.
              (js/document.getElementById "libra")
              {:type "line"
               :data
               {:labels (->> @store
                             :data
                             (map :date)
                             (into []))
                :datasets [{:label "Weight"
                            :data (->> @store
                                       :data
                                       (map :weight)
                                       (into []))
                            :fill false
                            :borderColor "rgb(75, 192, 192)"
                            :lineTension 0.1}]}
               :options
               {:scales
                {:x {:type "time"
                     :time {:unit "day"
                            :displayFormats {:day "MMM d"}}
                     :ticks {:stepSize 3}
                     :min (-> @store
                              :data
                              last
                              :date
                              (if
                                (date-fns/parse "yyyy-MM-dd" (js/Date.))
                                (js/Date.))
                              (date-fns/subDays 4))
                     :max (-> @store
                              :data
                              last
                              :date
                              (if
                                (date-fns/parse "yyyy-MM-dd" (js/Date.))
                                (js/Date.))
                              (date-fns/addDays 2))}
                 :y {:min (-> @store
                              :data
                              last
                              :weight
                              (or 100)
                              (- 25))

                     :max (-> @store
                              :data
                              last
                              :weight 
                              (or 100)
                              (+ 25))
                     :ticks {:stepSize 25}
                     :startAtZero true}}
                :plugins
                {:legend {:display false}
                 :zoom {:pan {:enabled true}
                        :zoom {:wheel {:enabled true}
                               :pinch {:enabled true}}}
                 :limits {:x {:minRange (* 7 24 60 60 1000)}}}}}))

(mobx/autorun
 (fn []
   (let [data (->> @store
                   :data
                   (map :weight)
                   (into []))]
     (set! (.. myChart -data -datasets -data) data)
     (set! (.. myChart -data -labels) (->> @store
                                           :data
                                           (map :date)
                                           (into [])))
     (myChart.update))))
