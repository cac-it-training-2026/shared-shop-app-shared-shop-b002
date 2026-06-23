package jp.co.sss.shop.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.OrderItemRepository;

/**
 * 料金計算用ユーティリティクラス
 */
public class PriceCalc {
	/**
	 * ダイナミックプライシングに基づいた販売価格を計算
	 * @param basePrice 基準価格
	 * @param stock 在庫数
	 * @param itemsSold 直近の注文数（数量合計）
	 * @return 計算後の販売価格
	 */
	public static int calculateDynamicPrice(int basePrice, int stock, long itemsSold) {
		// 在庫補正率の決定
		double stockRate;
		if (stock < 10) {
			stockRate = 1.20;
		} else if (stock <= 20) {
			stockRate = 1.10;
		} else if (stock <= 25) {
			stockRate = 1.00;
		} else if (stock <= 50) {
			stockRate = 0.90;
		} else {
			stockRate = 0.80;
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
	 * 商品情報にダイナミックプライシングに関連する情報を設定
	 * @param item 商品エンティティ
	 * @param itemBean 商品情報Bean
	 * @param itemsSold 直近の注文数（数量合計）
	 */
	public static void setDynamicPriceInfo(Item item, ItemBean itemBean, long itemsSold) {
		int dynamicPrice = calculateDynamicPrice(item.getPrice(), item.getStock(), itemsSold);
		itemBean.setPrice(dynamicPrice);

		// 基本価格と割引率をセット
		itemBean.setBasePrice(item.getPrice());
		if (dynamicPrice < item.getPrice()) {
			int discountRate = (int) Math.round((1.0 - (double) dynamicPrice / item.getPrice()) * 100);
			itemBean.setDiscountRate(discountRate);
		} else {
			itemBean.setDiscountRate(null);
		}
	}

	/**
	 * 商品情報リストにダイナミックプライシングに関連する情報を一括設定 (N+1問題対策)
	 * @param itemList 商品エンティティリスト
	 * @param itemBeanList 商品情報Beanリスト
	 * @param orderItemRepository 注文商品リポジトリ
	 */
	public static void setDynamicPriceInfoList(List<Item> itemList, List<ItemBean> itemBeanList, OrderItemRepository orderItemRepository) {
		if (itemList == null || itemList.isEmpty()) {
			return;
		}

		List<Integer> itemIds = itemList.stream().map(Item::getId).collect(Collectors.toList());
		java.sql.Date date = java.sql.Date.valueOf(LocalDate.now().minusDays(30));

		List<Object[]> results = orderItemRepository.countQuantitiesByItemIdsAndOrderInsertDateAfter(itemIds, date);
		Map<Integer, Long> itemsSoldMap = new HashMap<>();
		for (Object[] result : results) {
			itemsSoldMap.put((Integer) result[0], (Long) result[1]);
		}

		for (int i = 0; i < itemList.size(); i++) {
			Item item = itemList.get(i);
			ItemBean itemBean = itemBeanList.get(i);
			Long itemsSold = itemsSoldMap.getOrDefault(item.getId(), 0L);
			setDynamicPriceInfo(item, itemBean, itemsSold);
		}
	}

	/**
	 * 小計から注文した商品の合計金額を計算
	 *
	 * @param list
	 *            注文した商品情報
	 * @return 合計金額
	 */
	public static int orderItemBeanPriceTotalUseSubtotal(List<OrderItemBean> list) {
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
	public static int orderItemBeanPriceTotal(List<OrderItemBean> list) {
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
	public static int orderItemPriceTotal(List<OrderItem> list) {
		int total = 0;

		for (OrderItem orderItem : list) {
			total = total + (orderItem.getPrice() * orderItem.getQuantity() );
		}

		return total;
	}
}
