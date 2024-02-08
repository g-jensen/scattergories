(ns scattergories.room)

(def rooms (atom {}))

(def code-chars
  (->> (concat (range 48 58) (range 65 91))
    (map char)
    (remove #{\O \0 \1 \I \G \g})))

(defn new-code []
  (let [code (->> (repeatedly #(rand-nth code-chars))
               (take 6)
               (apply str))]
    (if-not (some #{code} (keys @rooms))
      code
      (new-code))))


(defn ws-create-room [{:keys [params] :as request}]
  (prn "request" request))