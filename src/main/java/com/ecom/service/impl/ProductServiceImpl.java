package com.ecom.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {   
        return productRepository.findAll();
    }

    @Override
    public boolean deleteProduct(int id) {
        Product product =  productRepository.findById(id).orElse(null);
        if(!ObjectUtils.isEmpty(product)){
            productRepository.delete(product);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public Product getProductById(int id) {
        Product product = productRepository.findById(id).orElse(null);
        return product;
    }

    @Override
    public Product updateProduct(Product product, MultipartFile image) {

        Product existProduct = getProductById(product.getId());

        String imageName = image.isEmpty() ? existProduct.getProductImage() : image.getOriginalFilename();

	    existProduct.setProductName(product.getProductName());
        existProduct.setProductDescription(product.getProductDescription());
        existProduct.setProductCategory(product.getProductCategory());
		existProduct.setProductPrice(product.getProductPrice());
        existProduct.setStock(product.getStock());
        existProduct.setProductImage(imageName);
        existProduct.setDiscount(product.getDiscount());
        double discountedPrice = (product.getProductPrice()*(100-product.getDiscount()))/100;
        existProduct.setDiscountPrice(discountedPrice);
        Product updateProduct = productRepository.save(existProduct);

        if(!ObjectUtils.isEmpty(updateProduct)){
            if(!image.isEmpty()){
                try {
                    File savefile = new ClassPathResource("static/images").getFile();

                    Path path = Paths.get(savefile.getAbsolutePath()+File.separator+"latest_product"+File.separator+image.getOriginalFilename());
                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
			
			return updateProduct;
		}
		return null;
        
        
    }
    
}
