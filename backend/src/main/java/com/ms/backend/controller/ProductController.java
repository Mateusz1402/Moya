package com.ms.backend.controller;

import com.ms.backend.dto.CreateProductRequest;
import com.ms.backend.dto.ProductDto;
import com.ms.backend.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService  productService;

    public ProductController(ProductService productService){ this.productService = productService;}

    @GetMapping("/get-all")
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}/get-by-id")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("{category}/get-by-category")
    public ResponseEntity<List<ProductDto>> getProductByCategory(@PathVariable String category){
        return ResponseEntity.ok(productService.getAllProductsByCategory(category));
    }

    @GetMapping("{name}/get-by-name")
    public ResponseEntity<ProductDto> getProductByName(@PathVariable String name){
        return ResponseEntity.ok(productService.getAllProductsByName(name));
    }

    @PostMapping("create")
    public ResponseEntity<ProductDto> createProduct(@RequestBody CreateProductRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("{id}/update")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody CreateProductRequest request){
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("{id}/delete")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
