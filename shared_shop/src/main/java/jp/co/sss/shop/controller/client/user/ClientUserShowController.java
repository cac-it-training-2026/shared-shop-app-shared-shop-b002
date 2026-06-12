package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserShowController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	HttpSession session;

	/**
	 * @param session
	 * @param model
	 * @return
	 * ユーザーの詳細情報を表示するメソッド
	 */

	//POSTで実装したがエラーだったのでGETで実装していたが、木村さんのメソッドと競合していた可能性あり。
	//11日に本来のPOSTに変更。現在は変わらずエラーだが、木村さんにメソッドを削除してもらったので、明日確認作業をする。

	@RequestMapping(path = "/client/user/detail")

	public String showDetailUser(Model model) {

		// セッションからログインしている会員の情報を取り出す
		UserBean loginUser = (UserBean) session.getAttribute("user");

		if (loginUser == null) {
			return "redirect:/syserror";
		}

		jp.co.sss.shop.entity.User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(),
				jp.co.sss.shop.util.Constant.NOT_DELETED);

		if (user == null) {
			return "redirect:/syserror";
		}

		// 3. 画面に渡すために、Entity(User) から Bean(UserBean) へデータを詰め替える
		UserBean userBean = new UserBean();
		org.springframework.beans.BeanUtils.copyProperties(user, userBean);

		// 4. 【重要】DBから取ってきた「全部入りのuserBean」をリクエストスコープに登録する！
		model.addAttribute("userBean", userBean);

		return "client/user/detail";
	}
}