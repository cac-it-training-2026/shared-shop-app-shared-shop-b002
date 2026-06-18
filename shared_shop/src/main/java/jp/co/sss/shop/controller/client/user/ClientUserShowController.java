package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

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
	 * @return 詳細画面の表示
	 */
	@RequestMapping(path = "/client/user/detail")
	public String showDetailUser(Model model) {

		// ログイン中の会員情報をセッションから取得
		UserBean loginUser = (UserBean) session.getAttribute("user");

		// ログイン情報がない場合はシステムエラーへリダイレクト。nullチェック
		if (loginUser == null) {
			return "redirect:/syserror";
		}

		// 論理削除されていないユーザーをDBから取得。
		User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), Constant.NOT_DELETED);

		// ユーザーが存在しない場合はシステムエラーへリダイレクト
		if (user == null) {
			return "redirect:/syserror";
		}

		// DBから取得したデータをuserBeanへコピー
		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(user, userBean);

		// 画面へ表示用データを渡すためにモデルに登録
		model.addAttribute("userBean", userBean);

		// 会員詳細画面へ遷移
		return "client/user/detail";
	}
}