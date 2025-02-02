package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.User;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

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
	public String index() {
		return "index";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@PostMapping("/saveuser")
	public String saveUser(@ModelAttribute User user, @RequestParam("profileImage") MultipartFile image, HttpSession session) throws IOException {
		// user.setUserImage(image.isEmpty() ? "default.jpg" : image.getOriginalFilename());
		String profileImage = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
		user.setUserImage(profileImage);

		User savedUser = userService.saveUser(user);
		if(!ObjectUtils.isEmpty(savedUser)) {
			
			File savefile = new ClassPathResource("static/images").getFile();
			Path path = Paths.get(savefile.getAbsolutePath() + File.separator+"user_images" + File.separator + image.getOriginalFilename());
			
			System.out.println("The path     :    "+ path);
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);	
			
			session.setAttribute("succMsg", "User registered successfully");
			
		}
		else{
			session.setAttribute("errorMsg", "Something went wrong");	
		}
		return "redirect:/register";
	}

	@GetMapping("/product")
	public String product(Model m, @RequestParam(value="category", defaultValue="") String category) {
		List<Category> categories = categoryService.getAllActiveCategory();
		List<Product> products = productService.getAllActiveProducts(category);
		m.addAttribute("categories", categories);
		m.addAttribute("products", products);
		m.addAttribute("paramvalue", category);
		return "product";
	}

	@GetMapping("/viewproduct/{id}")
	public String viewProduct(@PathVariable int id, Model m) {
		Product productById = productService.getProductById(id);
		m.addAttribute("product", productById);
		return "view_product";
	}

	
}
