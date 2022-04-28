package com.example.springboot.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;


import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;

@RestController
public class ProductController {
	
	@Autowired
	ProductRepository productRepository;
	
	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts(){
		List<ProductModel> productsList = productRepository.findAll();
		if(!productsList.isEmpty()) {
			for(ProductModel product : productsList) {
				UUID id = product.getIdProduct();
				product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
			}
		}
		return new ResponseEntity<List<ProductModel>>(productsList, HttpStatus.OK);
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<ProductModel> getOneProduct(@PathVariable(value="id") UUID id){
		Optional<ProductModel> productO = productRepository.findById(id);
		if(productO.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		productO.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products List"));
		return new ResponseEntity<ProductModel>(productO.get(), HttpStatus.OK);
	}
	
	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductModel product) {
		return new ResponseEntity<ProductModel>(productRepository.save(product), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/products/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable(value="id") UUID id) {
		Optional<ProductModel> productO = productRepository.findById(id);
		if(productO.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		productRepository.delete(productO.get());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("/products/{id}")
	public ResponseEntity<ProductModel> updateProduct(@PathVariable(value="id") UUID id,
													  @RequestBody @Valid ProductModel product) {
		Optional<ProductModel> productO = productRepository.findById(id);
		if(productO.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		product.setIdProduct(productO.get().getIdProduct());
		return new ResponseEntity<ProductModel>(productRepository.save(product), HttpStatus.OK);
	}

}
