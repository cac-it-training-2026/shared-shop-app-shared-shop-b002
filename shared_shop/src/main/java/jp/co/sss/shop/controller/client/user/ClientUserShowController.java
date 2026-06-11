package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserShowController {

	@Autowired
	private UserRepository userRepository;

	/**
	 * @param session
	 * @param model
	 * @return
	 * ユーザーの詳細情報を表示するメソッド
	 */
	@RequestMapping(path = "/client/user/detail", method = RequestMethod.GET)
	//セッションスコープに情報を保存。ModelでHTMLへデータを渡す。
	public String showDetailUser(HttpSession session, Model model) {

		//スコープから"user"でラベリングされたログインしてる会員の情報を取り出し、
		//loginUserへ代入
		UserBean loginUser = (UserBean) session.getAttribute("user");

		if (loginUser == null) {

			return "redirect:/";
		}

		session.removeAttribute("userForm");

		User user = userRepository.findById(loginUser.getId()).orElse(null);

		UserBean userBean = new UserBean();
		if (user != null) {
			BeanUtils.copyProperties(user, userBean);

		}

		model.addAttribute("userBean", userBean);

		return "client/user/detail";
	}

}