package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.exceptions.APIException;
import com.ecommerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.sb_ecom.mapper.ProductMapper;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.payload.ProductResponse;
import com.ecommerce.sb_ecom.repository.CategoryRepository;
import com.ecommerce.sb_ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean newProduct = true;

        List<Product> products = category.getProducts();

        for (Product p : products){
            if (p.getProductName().equals(productDTO.getProductName())){
                newProduct = false;
                break;
            }
        }

        if (newProduct) {

            Product product = productMapper.prodDTOToProd(productDTO);
            product.setCategory(category);
            Double specialPrice = product.getPrice() * (100 - product.getDiscount()) * 0.01;
            product.setImage("default.png");
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return productMapper.prodToDTO(savedProduct);
        }

        throw new APIException("Cannot add product. Product already exists!");
    }

    @Override
    public ProductResponse getAllProducts() {

        List<Product> productList = productRepository.findAll();

        List<ProductDTO>  productDTOS = productList.stream()
                                        .map(productMapper::prodToDTO)
                                        .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        List<Product> productList = productRepository.findByCategoryOrderByPriceAsc(category);

        List<ProductDTO>  productDTOS = productList.stream()
                .map(productMapper::prodToDTO)
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        return productResponse;

    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword) {

        List<Product> productList = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%');

        List<ProductDTO>  productDTOS = productList.stream()
                .map(productMapper::prodToDTO)
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        return productResponse;

    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {

        Product productDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        Product product = productMapper.prodDTOToProd(productDTO);

        productDB.setProductName(product.getProductName());
        productDB.setDescription(product.getDescription());
        productDB.setQuantity(product.getQuantity());
        productDB.setPrice(product.getPrice());
        productDB.setDiscount(product.getDiscount());
        productDB.setSpecialPrice(product.getSpecialPrice());

        Product productSaved = productRepository.save(productDB);
        return productMapper.prodToDTO(productSaved);

    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product savedProduct =  productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId) );

        productRepository.delete(savedProduct);

        return productMapper.prodToDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product savedProduct =  productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId) );

        String  fileName = fileService.uploadImage(path, image);

        savedProduct.setImage(fileName);

        Product updatedProduct =productRepository.save(savedProduct);

        return productMapper.prodToDTO(updatedProduct);
    }

}
