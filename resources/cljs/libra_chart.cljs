(ns libra-chart
  (:require
   ["chart.js" :as chart]
   ["chartjs-adapter-date-fns"]
   ["chartjs-plugin-zoom$default" :as zoom]
   ["chartjs-plugin-annotation$default" :as annotations]
   ["date-fns" :as date-fns]
   ["mobx" :as mobx]
   ["axios$default" :as axios]))

(apply (.-register chart/Chart) (into [] (concat chart/registerables [zoom annotations])))

(def store
  (mobx/makeAutoObservable
   (atom
    {:data []})))

(defn ->labels [data]
  (->> data
       (map :weight)
       (into [])))

(defn ->data [data]
  (->> data
       (map :date)
       (into [])))

(defn ->x-range [data]
  (let [min (-> data
                last
                :date
                (if (date-fns/parse "yyyy-MM-dd" (js/Date.)) (js/Date.))
                (date-fns/subDays 4))
        max (-> data
                last
                :date
                (if (date-fns/parse "yyyy-MM-dd" (js/Date.)) (js/Date.))
                (date-fns/addDays 2))]
    {:min min
     :max max}))

(defn ->y-range [data]
  (let [min (-> data
                last
                :weight
                (or 100)
                (- 25))
        max (-> data
                last
                :weight
                (or 100)
                (+ 25))]
    {:min min
     :max max}))

(def myChart (chart/Chart.
              (js/document.getElementById "libra")
              {:type "line"
               :data
               {:labels (->> @store
                             :data
                             (->labels))
                :datasets [{:label "Weight"
                            :data (->> @store
                                       :data
                                       (->data))
                            :fill false
                            :borderColor "rgb(75, 192, 192)"
                            :lineTension 0.1}]}
               :options
               {:scales
                {:x (merge {:type "time"
                            :time {:unit "day"
                                   :displayFormats {:day "MMM d"}}
                            :ticks {:stepSize 3}}
                           (->x-range (:data @store)))

                 :y (merge {:ticks {:stepSize 25}
                            :startAtZero true}
                           (->y-range (:data @store)))}
                :plugins
                {:legend {:display false}
                 :zoom {:pan {:enabled true}
                        :zoom {:wheel {:enabled true}
                               :pinch {:enabled true}}}
                 :limits {:x {:minRange (* 7 24 60 60 1000)}}}}}))

(-> (axios/get "/api/data"
               {:responseType "json"})
    (.then (fn [response]
             (swap! store assoc :data (-> response :data)))))

(mobx/autorun
 (fn []
   (let [data (->> @store
                   :data
                   (map :weight)
                   (into []))
         labels (->> @store
                     :data
                     (map :date)
                     (into []))]
     (js/console.log :data data :labels labels)
     (set! (.. (first (.. myChart -data -datasets)) -data) data)
     (set! (.. myChart -data -labels) labels)
     (myChart.update))))
