package jp.co.sss.shop.controller.client.coupon;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.entity.Coupon;
import jp.co.sss.shop.repository.CouponRepository;

/**
 * クーポン表示用コントローラ
 */
@Controller
public class ClientCouponShowController {

	@Autowired
	CouponRepository couponRepository;

	/**
	 * 有効なクーポン一覧を表示する
	 * @param model モデル
	 * @return クーポン一覧画面
	 */
	@RequestMapping(path = "/client/coupon/list", method = RequestMethod.GET)
	public String showCouponList(Model model) {
		List<Coupon> allCoupons = couponRepository.findAll();
		Timestamp now = new Timestamp(System.currentTimeMillis());

		// 有効かつ期間内のクーポンのみ抽出
		List<Coupon> activeCoupons = allCoupons.stream()
				.filter(c -> c.getEnabled() != null && c.getEnabled() == 1)
				.filter(c -> c.getStartDate() == null || now.after(c.getStartDate()))
				.filter(c -> c.getEndDate() == null || now.before(c.getEndDate()))
				.collect(Collectors.toList());

		model.addAttribute("coupons", activeCoupons);
		return "client/coupon/list";
	}
}
