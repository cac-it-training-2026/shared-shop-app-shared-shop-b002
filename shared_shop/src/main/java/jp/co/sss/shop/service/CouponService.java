package jp.co.sss.shop.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.sss.shop.entity.Coupon;
import jp.co.sss.shop.repository.CouponRepository;

/**
 * クーポン関連の処理を行うサービスクラス
 */
@Service
public class CouponService {

	@Autowired
	CouponRepository couponRepository;

	/**
	 * クーポンコードを検証し、有効なクーポンを返す
	 * @param couponCode クーポンコード
	 * @return 有効なクーポン。無効な場合はnull
	 */
	public Coupon validateCoupon(String couponCode) {
		// 有効フラグが1のクーポンを検索
		Coupon coupon = couponRepository.findByCouponCodeAndEnabled(couponCode, 1);

		if (coupon == null) {
			return null;
		}

		Timestamp now = new Timestamp(System.currentTimeMillis());

		// 利用期間のチェック
		if (coupon.getStartDate() != null && now.before(coupon.getStartDate())) {
			return null;
		}
		if (coupon.getEndDate() != null && now.after(coupon.getEndDate())) {
			return null;
		}

		return coupon;
	}

	/**
	 * 割引額を計算する
	 * @param coupon クーポン情報
	 * @param totalPrice 合計金額
	 * @return 割引額
	 */
	public Integer calculateDiscount(Coupon coupon, Integer totalPrice) {
		if (coupon == null || totalPrice == null) {
			return 0;
		}

		Integer discountAmount = 0;
		if ("FIXED".equals(coupon.getDiscountType())) {
			// 固定額割引
			discountAmount = coupon.getDiscountValue();
		} else if ("PERCENT".equals(coupon.getDiscountType())) {
			// 割合割引
			discountAmount = (int) (totalPrice * (coupon.getDiscountValue() / 100.0));
		}

		// 割引額が合計金額を超えないように調整
		if (discountAmount > totalPrice) {
			discountAmount = totalPrice;
		}

		return discountAmount;
	}
}
