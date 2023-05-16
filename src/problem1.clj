(ns problem1)
(def invoice (clojure.edn/read-string (slurp "invoice.edn")))
(defn tax-is-iva-19 [{rate :tax/rate category :tax/category}]
  (and (= rate 19)
       (= category :iva)
       ))
(defn retention-is-ret-fuente-1 [{rate :retention/rate category :retention/category}]
  (and (= rate 1)
       (= category :ret_fuente)
       ))
(defn valid-invoice [{taxes :taxable/taxes retentions :retentionable/retentions}]
  (let [has-iva-19 (some tax-is-iva-19 taxes)
        has-ret-fuente-1 (some retention-is-ret-fuente-1 retentions)]
    (or (and has-iva-19 (not has-ret-fuente-1)) (and (not has-iva-19) has-ret-fuente-1))))
(defn filter-invoice-items [{items :invoice/items}]
  (->> items
       (filter valid-invoice)))
(defn -main
  []
  (println (filter-invoice-items invoice))
  )


