(ns libra-chart
  (:require
   ["chart.js" :as chart]
   ["chartjs-adapter-date-fns"]
   ["chartjs-plugin-zoom$default" :as zoom]
   ["chartjs-plugin-annotation$default" :as annotations]
   ["date-fns" :as date-fns]
   ["mobx" :as mobx]
   ["axios$default" :as axios]
   ["regression$default" :as regression]))

(apply (.-register chart/Chart) (into [] (concat chart/registerables [zoom annotations])))

(def store
  (mobx/makeAutoObservable
   (atom
    {:data []
     :trend-data []})))

(defn calculate-trend-line [data]
  (when (seq data)
    (let [points (->> data
                      (map (fn [point]
                             [(-> point
                                  :date
                                  (date-fns/parse "yyyy-MM-dd" (js/Date.))
                                  (.getTime))
                              (:weight point)]))
                      (reverse)
                      (take 10)
                      (reverse)
                      (into []))
          result (regression/linear points)
          predict (fn [date]
                    (let [x (.getTime (if (string? date)
                                        (date-fns/parse date "yyyy-MM-dd" (js/Date.))
                                        date))]
                      (.predict result x)))
          today (js/Date.)
          last-date (-> data last :date)
          last-date-obj (date-fns/parse last-date "yyyy-MM-dd" (js/Date.))
          needs-prediction? (date-fns/isAfter today last-date-obj)]
      (js/console.log "needs-prediction?" needs-prediction?)
      (->> (if needs-prediction?
             (conj data {:date (date-fns/format today "yyyy-MM-dd")
                         :weight (second (predict today))
                         :predicted true})
             data)
           (map (fn [point] (assoc point :trend (second (predict (:date point))))))))))

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
                (#(if % (date-fns/parse % "yyyy-MM-dd" (js/Date.)) (js/Date.)))
                (date-fns/subDays 4))
        max (-> data
                last
                :date
                (#(if % (date-fns/parse % "yyyy-MM-dd" (js/Date.)) (js/Date.)))
                (date-fns/addDays 2))]
    {:min min
     :max max}))

(def y-range 10)
(defn ->y-range [data]
  (let [min (-> data
                last
                :weight
                (or 100)
                (- (/ y-range 2)))
        max (-> data
                last
                :weight
                (or 100)
                (+ (/ y-range 2)))]
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
                            :tension 0.6
                            :pointStyle "rectRot"}
                           {:label "Trend"
                            :data (->> @store
                                       :trend-data
                                       (->data))
                            :fill false
                            :cubicInterpolationMode "monotone"
                            :borderColor "rgb(255, 99, 132)"
                            :tension 0.6
                            :pointStyle "rectRot"}]}
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
             (let [data (-> response :data)
                   trend-data (calculate-trend-line data)]
               (swap! store assoc
                      :data data
                      :trend-data trend-data)))))

(mobx/autorun
 (fn []
   (let [trend-data (:trend-data @store)
         data (->> trend-data
                   (map :weight)
                   (into []))
         trend-line (->> trend-data
                         (map :trend)
                         (into []))
         labels (->> trend-data
                     (map :date)
                     (into []))
         y-range (->y-range (:data @store))]
     (set! (.. (first (.. myChart -data -datasets)) -data) data)
     (set! (.. (get (.. myChart -data -datasets) 1) -data) trend-line)
     (set! (.. myChart -data -labels) labels)
     ; (set! (.. myChart -options -scales -x -min) (:min x-range))
     ; (set! (.. myChart -options -scales -x -max) (:max x-range))
     (set! (.. myChart -options -scales -y -min) (:min y-range))
     (set! (.. myChart -options -scales -y -max) (:max y-range))
     (myChart.update))))
