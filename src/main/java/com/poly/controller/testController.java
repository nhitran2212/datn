package com.poly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class testController {
	@RequestMapping({ "index" })
	public String index() {

		return "index";
	}

	@RequestMapping("profile")
	public String profile() {

		return "Profile";
	}

	@RequestMapping("cart")
	public String cart() {

		return "cart";
	}

	@RequestMapping("changePass")
	public String change_Password() {

		return "Change_Password";
	}

	@RequestMapping("change-password-authenticated")
	public String change_password_authenticated() {
		return "Change_Password_Authenticated";
	}

	@RequestMapping("course_content")
	public String course_content() {

		return "course_content";
	}

	@RequestMapping("Crud_Categories")
	public String Crud_Categories() {
		return "Crud_Categories";
	}

	@RequestMapping("Crud_Course")
	public String Crud_Course() {
		return "Crud_Course";
	}

	@RequestMapping("Crud_Sale")
	public String Crud_Sale() {
		return "Crud_Sale";
	}

	@RequestMapping("Crud_User")
	public String Crud_User() {
		return "Crud_User";
	}

	@RequestMapping("forgotPass")
	public String forgotPass() {
		return "Forgot_Password";
	}

	@RequestMapping("Login_Signin")
	public String Login_Signin() {
		return "Login_Signin";
	}

	@RequestMapping("payment")
	public String payment() {
		return "Payment";
	}

	@RequestMapping("Statistic_Revenue")
	public String Statistic_Revenue() {
		return "Statistic_Revenue";
	}

	@RequestMapping("Statistic_Student")
	public String Statistic_Student() {
		return "Statistic_Student";
	}

	@RequestMapping("Statistic_Certificate")
	public String Statistic_Certificate() {
		return "Statistic_Certificate";
	}
	@RequestMapping("CourseInProgess")
	public String CourseInProgess() {
		return "CourseInProgess";
	}
	@RequestMapping("history-payment")
	public String History_Payment() {
		return "History_Payment";
	}

}
