package com.example.springboot.controllers;

import java.util.List;
import java.util.Optional;

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


import com.example.springboot.models.ProdutoModel;
import com.example.springboot.repositories.ProdutoRepository;

@RestController
public class ProdutoController {
	
	@Autowired
	ProdutoRepository produtoRepository;
	
	@GetMapping("/produtos")
	public ResponseEntity<List<ProdutoModel>> getAllProdutos(){
		List<ProdutoModel> produtosList = produtoRepository.findAll();
		if(!produtosList.isEmpty()) {
			for(ProdutoModel produto : produtosList) {
				long id = produto.getIdProduto();
				produto.add(linkTo(methodOn(ProdutoController.class).getOneProduto(id)).withSelfRel());
			}
		}
		return new ResponseEntity<List<ProdutoModel>>(produtosList, HttpStatus.OK);
	}
	
	@GetMapping("/produtos/{id}")
	public ResponseEntity<ProdutoModel> getOneProduto(@PathVariable(value="id") long id){
		Optional<ProdutoModel> produtoO = produtoRepository.findById(id);
		if(!produtoO.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		produtoO.get().add(linkTo(methodOn(ProdutoController.class).getAllProdutos()).withRel("Lista de Produtos"));
		return new ResponseEntity<ProdutoModel>(produtoO.get(), HttpStatus.OK);
	}
	
	@PostMapping("/produtos")
	public ResponseEntity<ProdutoModel> saveProduto(@RequestBody @Valid ProdutoModel produto) {
		return new ResponseEntity<ProdutoModel>(produtoRepository.save(produto), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/produtos/{id}")
	public ResponseEntity<?> deleteProduto(@PathVariable(value="id") long id) {
		Optional<ProdutoModel> produtoO = produtoRepository.findById(id);
		if(!produtoO.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		produtoRepository.delete(produtoO.get());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("/produtos/{id}")
	public ResponseEntity<ProdutoModel> updateProduto(@PathVariable(value="id") long id,
										@RequestBody @Valid ProdutoModel produto) {
		Optional<ProdutoModel> produtoO = produtoRepository.findById(id);
		if(!produtoO.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		produto.setIdProduto(produtoO.get().getIdProduto());
		return new ResponseEntity<ProdutoModel>(produtoRepository.save(produto), HttpStatus.OK);
	}

}
