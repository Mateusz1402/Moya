package com.ms.backend.service;

import com.ms.backend.dto.CreateProductRequest;
import com.ms.backend.dto.ProductDto;
import com.ms.backend.exception.BusinessLogicException;
import com.ms.backend.exception.ResourceNotFoundException;
import com.ms.backend.model.Product;
import com.ms.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class ProductService {

    private static final String WATER = "WATER";
    private static final String BEER = "BEER";
    private static final String HOTDOG = "HOTDOG";
    private static final String SWEET = "SWEET";
    private static final String TOBACCO = "TOBACCO";

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }


    public List<ProductDto> getAllProducts(){
        return productRepository.findAll().stream().map(this::toProductDto).toList();
    }

    public ProductDto getProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tank not found with id" + id));
        return toProductDto(product);
    }

    public List<ProductDto> getAllProductsByCategory(String category){
        return productRepository.findAllByCategory(category).stream().map(this::toProductDto).toList();
    }


    public ProductDto getAllProductsByName(String name){
        return toProductDto(productRepository.findAllByName(name));
    }

    @Transactional
    public ProductDto createProduct(CreateProductRequest request){
        Product product = Product.builder()
                .sku(request.sku())
                .name(request.name())
                .category(request.category())
                .price(request.price())
                .stockQuantity(request.stock_quantity())
                .build();
        return toProductDto(productRepository.save(product));
    }

    @Transactional
    public ProductDto updateProduct(Long id, CreateProductRequest request){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find the product id " + id));

        product.setName(request.name());
        product.setSku(request.sku());
        product.setCategory(request.category());
        product.setPrice(request.price());
        product.setStockQuantity(request.stock_quantity());

        return toProductDto(product);
    }

    public void deleteProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException("Can not find product with such id " + id));
    }




    private ProductDto toProductDto(Product product){
        return new ProductDto(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity());
    }

}
