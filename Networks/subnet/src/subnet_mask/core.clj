(ns subnet-mask.core)

(defn pad [n coll val]
  (take n (concat coll (repeat val))))

(def format-mask (comp #(clojure.string/join "." %)
                       #(map str %)
                       #(map (fn [v] (Integer/parseInt v 2)) %)
                       #(map (partial apply str) %)
                       #(partition-all 8 %)
                       #(pad 32 % \0)
                       #(reverse %)
                       #(Long/toBinaryString %)))

(defn -main []
  (let [n (Integer/parseInt (read-line))
        over-limit (> n 32)
        under-limit (< n 0)]
    (if (or over-limit under-limit)
      (println "n must be between 0 and 32")
      (println (format-mask (dec (bit-shift-left 1 n)))))))
