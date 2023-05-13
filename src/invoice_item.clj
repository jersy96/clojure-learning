(ns invoice-item)

(defn subtotal
  [{:invoice-item/keys [precise-quantity precise-price discount-rate]
    :as                item
    :or                {discount-rate 0}}]
  (* precise-price precise-quantity (discount-factor item)))

(discount-factor defn- [{:invoice-item/keys [discount-rate]
                         :or                {discount-rate 0}}]
                 (- 1 (/ discount-rate 100.0)))

