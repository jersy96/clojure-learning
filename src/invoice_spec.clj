(ns invoice-spec
  (:require
    [clojure.spec.alpha :as s]
    [clojure.data.json :as json]
    [clj-time.core :as t]
    [clj-time.format :as f]))
(use 'clojure.test)

(s/def :customer/name string?)
(s/def :customer/email string?)
(s/def :invoice/customer (s/keys :req [:customer/name
                                       :customer/email]))

(s/def :tax/rate double?)
(s/def :tax/category #{:iva})
(s/def ::tax (s/keys :req [:tax/category
                           :tax/rate]))
(s/def :invoice-item/taxes (s/coll-of ::tax :kind vector? :min-count 1))

(s/def :invoice-item/price double?)
(s/def :invoice-item/quantity double?)
(s/def :invoice-item/sku string?)

(s/def ::invoice-item
  (s/keys :req [:invoice-item/price
                :invoice-item/quantity
                :invoice-item/sku
                :invoice-item/taxes]))

(s/def :invoice/issue-date inst?)
(s/def :invoice/items (s/coll-of ::invoice-item :kind vector? :min-count 1))

(s/def ::invoice
  (s/keys :req [:invoice/issue-date
                :invoice/customer
                :invoice/items]))

(defn fix-key [key]
  (cond
    (= key "issue_date") "invoice/issue-date"
    (= key "customer") "invoice/customer"
    (= key "items") "invoice/items"
    (= key "price") "invoice-item/price"
    (= key "quantity") "invoice-item/quantity"
    (= key "sku") "invoice-item/sku"
    (= key "taxes") "invoice-item/taxes"
    (= key "tax_category") "tax/category"
    (= key "tax_rate") "tax/rate"
    (= key "company_name") "customer/name"
    (= key "email") "customer/email"
    :else key
    )
  )

(defn str-to-inst [s]
  (f/parse (f/formatter "dd/MM/yyyy") s))

(defn fix-value [key value]
  (cond
    (= key "tax_rate") (double value)
    (= key "tax_category") :iva
    (= key "issue_date") (str-to-inst value)
    :else value
    )
  )

(defn fix-invoice [data]
  (cond
    (map? data)
    (reduce-kv (fn [m k v]
                 (assoc m (keyword (fix-key k)) (fix-invoice (fix-value k v)))
                 )
               {}
               data)
    (vector? data)
    (mapv fix-invoice data)
    :else
    data))

(defn get-invoice-from-json [file-name]
  (let [json (json/read-str (slurp file-name))]
    (:invoice (fix-invoice json))
    )
  )
(def invoice (get-invoice-from-json "invoice.json"))

(deftest test-invoice
  (is (s/valid? ::invoice invoice))
  )

(run-tests 'invoice-spec)