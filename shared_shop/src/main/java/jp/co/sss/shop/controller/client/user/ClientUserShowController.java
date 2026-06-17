package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員詳細表示用コントローラー
 */
@Controller
public class ClientUserShowController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	HttpSession session;

	/**
	 * 会員詳細画面を表示するメソッド
	 *
	 * @param model 画面へデータを渡すためのモデル
	 * @return 遷移先画面のパス
	 */
	@RequestMapping(path = "/client/user/detail", method = RequestMethod.GET)
	public String showDetailUser(Model model) {

		// ログイン中の会員情報をセッションから取得
		UserBean loginUser = (UserBean) session.getAttribute("user");

		// ログイン情報がない場合はシステムエラーへリダイレクト
		if (loginUser == null) {
			return "redirect:/syserror";
		}

		// 論理削除されていないユーザーをDBから取得
		jp.co.sss.shop.entity.User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(),
				jp.co.sss.shop.util.Constant.NOT_DELETED);

		// ユーザーが存在しない場合はシステムエラーへリダイレクト
		if (user == null) {
			return "redirect:/syserror";
		}

		// EntityからBeanへデータをコピー
		UserBean userBean = new UserBean();
		org.springframework.beans.BeanUtils.copyProperties(user, userBean);

		// 画面へ表示用データを渡すためにモデルに登録
		model.addAttribute("userBean", userBean);

		// 会員詳細画面へ遷移
		return "client/user/detail";
	}
}