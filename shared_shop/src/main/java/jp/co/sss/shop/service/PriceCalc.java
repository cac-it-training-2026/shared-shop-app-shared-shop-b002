package jp.co.sss.shop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.OrderItem;

/**
 * 料金計算用クラス
 *
 * @author System Shared
 */
@Service
public class PriceCalc {
	/**
	 * ダイナミックプライシングに基づいた販売価格を計算
	 * @param item 商品エンティティ
	 * @param itemsSold 直近の注文数（数量合計）
	 * @return 計算後の販売価格
	 */
	public int calculateDynamicPrice(Item item, long itemsSold) {
		int basePrice = item.getPrice();
		int stock = item.getStock();

		// 在庫補正率の決定
		double stockRate;
		if (stock < 10) {
			stockRate = 1.20;
		} else if (stock <= 30) {
			stockRate = 1.10;
		} else if (stock <= 100) {
			stockRate = 1.00;
		} else {
			stockRate = 0.90;
		}

		// 注文補正率の決定
		double orderRate;
		if (itemsSold <= 20) {
			orderRate = 1.00;
		} else if (itemsSold <= 50) {
			orderRate = 1.10;
		} else {
			orderRate = 1.15;
		}

		// 販売価格の算出
		double calculatedPrice = basePrice * stockRate * orderRate;

		// 制約：0円以上、基本価格の200%以下
		double maxPrice = basePrice * 2.0;
		if (calculatedPrice < 0) {
			calculatedPrice = 0;
		} else if (calculatedPrice > maxPrice) {
			calculatedPrice = maxPrice;
		}

		return (int) Math.round(calculatedPrice);
	}

	/**
	 * 小計から注文した商品の合計金額を計算
	 *
	 * @param list
	 *            注文した商品情報
	 * @return 合計金額
	 */
	public int orderItemBeanPriceTotalUseSubtotal(List<OrderItemBean> list) {
		int total = 0;

		for (OrderItemBean bean : list) {
			total = total + bean.getSubtotal();
		}

		return total;
	}

	/**
	 * 単価と注文した商品の合計金額を計算
	 *
	 * @param list
	 *            注文した商品情報
	 * @return 合計金額
	 */
	public int orderItemBeanPriceTotal(List<OrderItemBean> list) {
		int total = 0;

		for (OrderItemBean orderItemBean : list) {
			total = total + (orderItemBean.getPrice() * orderItemBean.getOrderNum());
		}

		return total;
	}

	/**
	 * 注文時の単価と商品個数の合計金額を計算
	 *
	 * @param list
	 *            注文した商品情報
	 * @return 合計金額
	 */
	public int orderItemPriceTotal(List<OrderItem> list) {
		int total = 0;

		for (OrderItem orderItem : list) {
			total = total + (orderItem.getPrice() * orderItem.getQuantity() );
		}

		return total;
	}
}
