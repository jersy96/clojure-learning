(ns invoice-item
  (:require
    [clojure.spec.alpha :as s]
    ))

(use 'clojure.test)

(defn discount-factor [{:keys [discount-rate]
                         :or                {discount-rate 0}}]
                 (- 1 (/ discount-rate 100.0)))

(defn subtotal
  [{:keys [precise-quantity precise-price discount-rate]
    :as                item
    :or                {discount-rate 0}}]
  (* precise-price precise-quantity (discount-factor item)))

(s/def :is/number number?)

(deftest test-is-number
  (let [item {:precise-quantity 10
              :precise-price 10
              :discount-rate 20}]
    (is (s/valid? :is/number (subtotal item)))
    ))
(deftest test-with-discount-rate
  (let [item {:precise-quantity 10
              :precise-price 10
              :discount-rate 20}]
    (is (= 80.0 (subtotal item)))
    ))

(deftest test-discount-rate-default-value
  (let [item {:precise-quantity 10
              :precise-price 10}]
    (is (= 100.0 (subtotal item)))
    ))

(deftest test-with-discount-rate-0
  (let [item {:precise-quantity 10
              :precise-price 10
              :discount-rate 0}]
    (is (= 100.0 (subtotal item)))
    ))

(deftest test-with-quantity-0
  (let [item {:precise-quantity 0
              :precise-price 10
              :discount-rate 20}]
    (is (= 0.0 (subtotal item)))
    ))

(deftest test-with-price-0
  (let [item {:precise-quantity 10
              :precise-price 0
              :discount-rate 20}]
    (is (= 0.0 (subtotal item)))
    ))

(deftest test-without-quantity
  (let [item {:precise-price 10
              :discount-rate 20}]
    (is (thrown? NullPointerException (subtotal item)))
    ))

(deftest test-without-price
  (let [item {:precise-quantity 10
              :discount-rate 20}]
    (is (thrown? NullPointerException (subtotal item)))
    ))

(deftest test-only-with-discount-rate
  (let [item {:discount-rate 20}]
    (is (thrown? NullPointerException (subtotal item)))
    ))

(deftest test-with-empty-item
  (let [item {}]
    (is (thrown? NullPointerException (subtotal item)))
    ))

(deftest test-with-negative-quantity
  (let [item {:precise-quantity -10
              :precise-price 10
              :discount-rate 20}]
    (is (= -80.0 (subtotal item)))
    ))

(deftest test-with-negative-price
  (let [item {:precise-quantity 10
              :precise-price -10
              :discount-rate 20}]
    (is (= -80.0 (subtotal item)))
    ))

(deftest test-with-negative-discount-rate
  (let [item {:precise-quantity 10
              :precise-price 10
              :discount-rate -20}]
    (is (= 120.0 (subtotal item)))
    ))

(deftest test-with-nil-item
  (let [item nil]
    (is (thrown? NullPointerException (subtotal item)))
    ))

(deftest test-with-discount-rate-100
  (let [item {:precise-quantity 10
              :precise-price 10
              :discount-rate 100}]
    (is (= 0.0 (subtotal item)))
    ))

(deftest test-with-discount-greater-than-100
  (let [item {:precise-quantity 10
              :precise-price 10
              :discount-rate 200}]
    (is (= -100.0 (subtotal item)))
    ))
(run-tests 'invoice-item)