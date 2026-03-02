package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {

	final ProductRepository productRepository;

	public ProductController(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts(){
		List<ProductModel> productsList = productRepository.findAll();
		productsList.forEach(product ->
				product.add(linkTo(methodOn(ProductController.class).getOneProduct(product.getIdProduct())).withSelfRel())
		);
		return ResponseEntity.status(HttpStatus.OK).body(productsList);
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getOneProduct(@PathVariable(value="id") UUID id){
		return productRepository.findById(id)
				.map(product -> {
					product.add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products List"));
					return ResponseEntity.status(HttpStatus.OK).body((Object) product);
				})
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found."));
	}

	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(productRepository.save(productRecordDto.convertToProductModel()));
	}


	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> deleteProduct(@PathVariable(value="id") UUID id) {
		return productRepository.findById(id)
				.map(product -> {
					productRepository.delete(product);
					return ResponseEntity.status(HttpStatus.OK).body((Object) "Product deleted successfully.");
				})
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found."));
	}
	
	@PutMapping("/products/{id}")
	public ResponseEntity<Object> updateProduct(@PathVariable(value="id") UUID id,
													  @RequestBody @Valid ProductRecordDto productRecordDto) {
		return productRepository.findById(id)
				.map(product -> {
					BeanUtils.copyProperties(productRecordDto, product);
					return ResponseEntity.status(HttpStatus.OK).body((Object) productRepository.save(product));
				})
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found."));
	}

}
