(ns eines-cli.main
  (:require [cljs.reader]
            [eines-cli.eines.client :as eines]
            ["readline" :as readline]))

(enable-console-print!)

(defn on-message [& args] (prn :on-message args))
(defn on-connect [& args] (prn :on-connect args))
(defn on-error [& args] (prn :on-error args))
(defn on-close [& args] (prn :on-close args))

(defn main []
  (if-let [url (aget js/process.argv 2)]
    (let [e (eines/init! {:on-message on-message
                          :on-connect on-connect
                          :on-error   on-error
                          :on-close   on-close
                          :url url})
          rl (.createInterface readline
                               #js {:input js/process.stdin
                                    :output js/process.stdout
                                    :prompt "e> "})]
      (doto rl
        (.prompt)
        (.on "line"
             (fn [line]
               (let [value (cljs.reader/read-string line)]
                 (cond
                   (map? value) (eines/send! value)
                   (some? value) (println "Eines can only send maps!")))
               (.prompt rl)))
        (.on "close"
             (fn [] (js/process.exit 0)))))
    (do
      (println "Usage: eines-cli <URL>")
      (js/process.exit 1))))
