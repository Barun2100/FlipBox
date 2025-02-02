package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecom.model.Category;
import com.ecom.model.User;
import com.ecom.service.CategoryService;
import com.ecom.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
	private UserService userService;

    @Autowired
    private CategoryService categoryService;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			User user = userService.getUserByEmail(p.getName());
			m.addAttribute("user", user);
		}

        List<Category> categories = categoryService.getAllActiveCategory();
		m.addAttribute("categories", categories);
	}

    @GetMapping("/")
    public String home() {
        return "user/home";
    }
    
}
