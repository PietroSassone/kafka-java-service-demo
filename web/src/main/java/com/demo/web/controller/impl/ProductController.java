package com.demo.web.controller.impl;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.service.exception.ProductNotFoundException;
import com.demo.service.model.ProductModel;
import com.demo.service.service.ProductService;
import com.demo.web.controller.assembler.ProductModelAssembler;
import com.demo.web.entity.ProductEntity;
import com.demo.web.payload.request.ProductRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    private final ProductModelAssembler productModelAssembler;

    @Autowired
    public ProductController(final ProductService productService, final ProductModelAssembler productModelAssembler) {
        this.productService = productService;
        this.productModelAssembler = productModelAssembler;
    }

    @PostMapping("/createProduct")
    public ResponseEntity<?> createProduct(@Valid @RequestBody final ProductRequest createProductRequest) {
        log.info("Create Product Request received: {}", createProductRequest);

        if (productService.findByProductName(createProductRequest.getProductName()).isPresent()) {
            throw new DataIntegrityViolationException("Product already exists.");
        }

        final ProductEntity newProduct = new ProductEntity(createProductRequest.getProductName(), createProductRequest.getPrice());
        final ProductModel productModel = productModelAssembler.toModel(productService.saveProduct(newProduct));

        return ResponseEntity
            .created(productModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(productModel);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody final ProductRequest productRequest, @PathVariable final Long id) {
        log.info("Update Product Request received: {}", productRequest.toString());
        final ProductEntity updatedProduct = productService.findByProductId(id)
            .map(product -> {
                product.setProductName(productRequest.getProductName());
                product.setPrice(productRequest.getPrice());
                return productService.saveProduct(product);
            })
            .orElseGet(() -> {
                log.warn("The product form the update request does not exist. Creating it now.");
                final ProductEntity newProduct = new ProductEntity(productRequest.getProductName(), productRequest.getPrice());
                newProduct.setId(id);
                return productService.saveProduct(newProduct);
            });

        final ProductModel productModel = productModelAssembler.toModel(updatedProduct);
        log.info("Updated product: {}", updatedProduct);

        return ResponseEntity
            .created(productModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(productModel);
    }

    @GetMapping("/{id}/getProduct")
    public ResponseEntity<ProductModel> getProduct(@PathVariable final Long id) {
        log.info("Get Product Request received for id: {}", id);

        return productService.findByProductId(id)
            .map(productModelAssembler::toModel)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @GetMapping("/products")
    public ResponseEntity<CollectionModel<ProductModel>> getAllProducts() {
        return new ResponseEntity<>(productModelAssembler.toCollectionModel(productService.getAllProducts()), HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable final Long id) {
        log.info("Delete Product Request received for user with id: {}", id);
        final ProductEntity userToDelete = productService.findByProductId(id).orElseThrow(() -> new ProductNotFoundException(id));

        productService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
