(ns libra.app.drivers.add
  (:require
   [borkdude.html :as h]
   [integrant.core :as ig]
   [libra.infra.ring :as response]
   [libra.utils.dep-macro :refer [defact]]
   [libra.ports.data :as data]
   [libra.infra.html :as hh]))

(defact ->page
  [layout repo] [req]
  (let [{:keys [weight]} (data/last-data repo)]

    (->> [:div {:class "container w-full h-full flex flex-col justify-center items-center"}
          [:form {:class "flex flex-col justify-center items-center w-full h-full"
                  :method "POST"
                  :action "/weight/add"}
           [:div {:class "relative mb-3"}
            [:input {:_ (hh/hyper 
                          "on blur "
                          "  if target's value is empty "
                          "    remove [@data-libra-input-state-active=true] "
                          "  end "
                          "end "
                          "on focus add [@data-libra-input-state-active=true] ")
                     :id "weight"
                     :type "number"
                     :name "weight"
                     :value (if weight weight "")
                     :class (str "peer block min-h-[auto] w-full rounded border-0 bg-transparent "
                                 "px-3 py-[0.32rem] leading-[1.6] outline-none "
                                 "transition-all duration-200 ease-linear "
                                 "focus:placeholder:opacity-100 peer-focus:text-primary data-[libra-input-state-active]:placeholder:opacity-100 "
                                 "motion-reduce:transition-none [&:not([data-libra-input-placeholder-active])]:placeholder:opacity-0")
                     :& (if weight {:data-libra-input-state-active "true"} {})}]
            [:label {:for "weight"
                     :class (str "pointer-events-none absolute left-3 top-0 mb-0 "
                                 "max-w-[90%] origin-[0_0] truncate pt-[0.37rem] leading-[1.6] "
                                 "text-neutral-500 "
                                 "transition-all duration-200 ease-out peer-focus:-translate-y-[0.9rem] peer-focus:scale-[0.8] peer-focus:text-primary "
                                 "peer-data-[libra-input-state-active]:-translate-y-[0.9rem] peer-data-[libra-input-state-active]:scale-[0.8] "
                                 "motion-reduce:transition-none")}
             "Weight"]
            [:button {:type "submit"
                      :class (str "inline-block rounded bg-secondary "
                                  "px-6 pb-2 pt-2.5 text-xs font-medium uppercase "
                                  "leading-normal text-black shadow-primary-3 transition "
                                  "duration-150 ease-in-out "
                                  "hover:bg-primary-accent-300 hover:shadow-primary-2 focus:bg-primary-accent-300 "
                                  "focus:shadow-primary-2 focus:outline-none focus:ring-0 active:bg-primary-600 "
                                  "active:shadow-primary-2 motion-reduce:transition-none  hover:shadow-dark-strong "
                                  "focus:shadow-dark-strong active:shadow-dark-strong")}
             "Submit"]]]
          (hh/script
           "text/javascript"
           (str "
                 const input = document.getElementById('weight');
                  input.addEventListener('blur', function(e) {
                    if (e.target.value === '') {
                      e.target.removeAttribute('value');
                    }
                  });
                 "))]
         (h/html)
         (layout req)
         (response/response))))

(defact ->post-weight
  [repo] [req]
  (let [weight (get-in req [:params :weight])]
    (println "weight" weight)
    (data/add-data repo weight)
    (response/redirect "/")))

(defmethod ig/init-key ::routes
  [_ {:keys [layout repo]}]
  [{:path "/weight/add"
    :method :get
    :response (->page layout repo)}
   {:path "/weight/add"
    :method :post
    :response (->post-weight repo)}])


(comment
  (str (h/html [:div {:class (hh/hyper "foo " "bar")}])))
