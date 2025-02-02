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
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/admin")
public class AdminController {

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
		return "admin/admin-index";
	}

	//Start : Product related mappings

	@GetMapping("/addproduct")
	public String addProduct(Model m) {
		List<Category> categories=categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		return "admin/add-product";
	}

	@PostMapping("/saveproduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, @RequestParam("active") boolean active, HttpSession session) throws IOException {
		
		product.setProductImage(image.isEmpty() ? "default.jpg" : image.getOriginalFilename());
		product.setDiscount(0);
		product.setDiscountPrice(product.getProductPrice());

		product.setActive(active);

		Product saveProduct = productService.saveProduct(product);
		if(!ObjectUtils.isEmpty(saveProduct)){

			File savefile = new ClassPathResource("static/images").getFile();

			Path path = Paths.get(savefile.getAbsolutePath()+File.separator+"latest_product"+File.separator+image.getOriginalFilename());
			//System.out.println("The path     :    "+ path);
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			session.setAttribute("succMsg", "Product saved successfully!");
		}
		else{
			session.setAttribute("errorMsg", "Something went wrong!");
		}
		return "redirect:/admin/addproduct"; 
	}

	@GetMapping("/products")
	public String viewProduct(Model m){
		m.addAttribute("products", productService.getAllProducts());
		return "admin/product";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session){
		boolean deleteProduct = productService.deleteProduct(id);
		if(deleteProduct){
			session.setAttribute("succMsg", "Product successfully deleted !");
		}
		else{
			session.setAttribute("errorMsg", "Something went wrong");
		}
		return "redirect:/admin/products";	
	}

	@GetMapping("/loadEditProduct/{id}")
	public String loadEditProduct(@PathVariable int id, Model m){
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit-product";
	}

	@PostMapping("/editProduct")
	public String editProduct(@ModelAttribute Product product, @RequestParam ("userImage") MultipartFile image, HttpSession session, Model m){
		
		if(product.getDiscount()<0 || product.getDiscount()>100){
			session.setAttribute("errorMsg", "Invalid discount !!");
		}
		else{
			Product updateProduct = productService.updateProduct(product, image);

			if(!ObjectUtils.isEmpty(updateProduct)){
				session.setAttribute("succMsg", "Product updated successfully!");
			}
			else{
				session.setAttribute("errorMsg", "Something went wrong!");
			}
		}

		return "redirect:/admin/loadEditProduct/"+product.getId();
	}



	//End : Product related mappings

	//Start : Category related mappings

	@GetMapping("/category")
	public String category(Model m) {
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/category";
	}

	@PostMapping("/savecategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, @RequestParam("isActive") boolean isActive,
			HttpSession session) throws IOException {
		
		
		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);
		boolean existCategory = categoryService.existCategory(category.getName());

		if (existCategory) {
			session.setAttribute("errorMsg", "Category name already exist");
		} else {
			
			//active status was automatically showing false everytime. even when it was rebdering as true also.
			category.setActive(isActive);

			Category saveCategory = categoryService.saveCategory(category);

			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not saved ! , internal server error.");
			} else {

				File savefile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(savefile.getAbsolutePath()+File.separator+"category"+File.separator+file.getOriginalFilename());
				
				//System.out.println("The path     :    "+ path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				session.setAttribute("succMsg", "saved successfully!");
			}
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session){
		boolean deleteCategory = categoryService.deleteCategory(id);
		if(deleteCategory){
			session.setAttribute("succMsg", "Category successfully deleted !");
		}
		else{
			session.setAttribute("errorMsg", "Something went wrong");
		}
		return "redirect:/admin/category";	
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m){
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit-category";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("isActive") boolean isActive, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException{
		
		Category existCategory = categoryService.getCategoryById(category.getId());
		
		String imageName = file.isEmpty() ? existCategory.getImageName() : file.getOriginalFilename();
		existCategory.setImageName(imageName);
		
		if(!ObjectUtils.isEmpty(category)){
			existCategory.setName(category.getName());
			existCategory.setActive(isActive);
		}
		
		Category updateCategory = categoryService.saveCategory(existCategory);
		
		if(!ObjectUtils.isEmpty(updateCategory)){

			if(!file.isEmpty()){
				File savefile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(savefile.getAbsolutePath()+File.separator+"category"+File.separator+file.getOriginalFilename());
				
				System.out.println("The path     :    "+ path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			session.setAttribute("succMsg", "Category updated successfully!");
		}
		else{
			session.setAttribute("errorMsg", "Something went wrong!");
		}
		
		return "redirect:/admin/loadEditCategory/"+category.getId();
	}

	//End : category related mappings

}
