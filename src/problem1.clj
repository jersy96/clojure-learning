(ns problem1)
(def invoice (clojure.edn/read-string (slurp "invoice.edn")))
(defn filter-invoice-items [invoice]
  (->> invoice
       (:invoice/items)
       (filter (fn [item]
                 (let [taxes (:taxable/taxes item)
                       has-iva-19 (some #(and (= (:tax/rate %) 19) (= (:tax/category %) :iva)) taxes)
                       retentions (:retentionable/retentions item)
                       has-ret-fuente-1 (some #(and (= (:retention/rate %) 1) (= (:retention/category %) :ret_fuente)) retentions)]
                   (or (and has-iva-19 (not has-ret-fuente-1)) (and (not has-iva-19) has-ret-fuente-1)))))))
(defn -main
  "Punto de entrada del programa"
  []
  (println (filter-invoice-items invoice))
  )


