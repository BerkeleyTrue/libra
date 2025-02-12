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
    {:data
     [{:date "2024-01-01" :weight 256}
      {:date "2024-01-04" :weight 254}
      {:date "2024-01-07" :weight 253}
      {:date "2024-01-10" :weight 251}
      {:date "2024-01-13" :weight 249}
      {:date "2024-01-16" :weight 248}
      {:date "2024-01-19" :weight 245}
      {:date "2024-01-22" :weight 243}
      {:date "2024-01-25" :weight 240}
      {:date "2024-01-28" :weight 238}
      {:date "2024-01-31" :weight 235}
      {:date "2024-02-03" :weight 233}
      {:date "2024-02-06" :weight 231}
      {:date "2024-02-09" :weight 229}
      {:date "2024-02-12" :weight 227}
      {:date "2024-02-15" :weight 225}
      {:date "2024-02-18" :weight 223}
      {:date "2024-02-21" :weight 222}
      {:date "2024-02-24" :weight 221}
      {:date "2024-02-27" :weight 220}]})))

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
                              (date-fns/parse "yyyy-MM-dd" (js/Date.))
                              (date-fns/subDays 4))
                     :max (-> @store
                              :data
                              last
                              :date
                              (date-fns/parse "yyyy-MM-dd" (js/Date.))
                              (date-fns/addDays 2))}
                 :y {:min (-> @store
                              :data
                              last
                              :weight
                              (- 25))

                     :max (-> @store
                              :data
                              last
                              :weight
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
