package com.ecom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@GetMapping("/")
	public String index() {
		return "admin/admin-index";
	}

	@GetMapping("/addproduct")
	public String addProduct() {
		return "admin/add-product";
	}

	@GetMapping("/addcategory")
	public String addCategory() {
		return "admin/add-category";
	}

}
