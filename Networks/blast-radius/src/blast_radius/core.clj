(ns blast-radius.core
  (:require [clojure.string :refer [split-lines split]]
            [clojure.java.io :refer [resource]]))

(defn read-ips []
  (slurp (resource "blocked-networks.txt")))

(defn collect-ips [file-contents]
  (split-lines file-contents))

(defn parse-ip [ip]
  (let [[addr mask] (split ip #"/")]
    {:address addr :mask mask}))

(defn pad [n coll val]
  (take n (concat coll (repeat val))))

(defn ip-to-number [ip]
  (as-> (split ip #"\.") $
        (map #(Integer/parseInt %) $)
        (map #(Integer/toBinaryString %) $)
        (map #(pad 8 % \0) $)
        (map reverse $)
        (map (partial apply str) $)
        (apply str $)
        (Long/parseLong $ 2)))

(def format-mask (comp #(Long/parseLong % 2)
                       #(apply str %)
                       #(map (fn [v] (if (= v \0) \1 \0)) %)
                       #(pad 32 % \0)
                       #(reverse %)
                       #(Long/toBinaryString %)))

(defn collect-subnet [subnet]
  (let [{:keys [address mask]} subnet
        start (ip-to-number address)
        end (bit-or start (format-mask (dec (bit-shift-left 1 (Integer/parseInt mask)))))]
    (range start (inc end))))

(defn -main []
  (let [subnets (->> (read-ips)
                     (collect-ips)
                     (map parse-ip)
                     (map collect-subnet)
                     (flatten)
                     (set)
                     (count))]
    (println "Banned addresses: " subnets)))
