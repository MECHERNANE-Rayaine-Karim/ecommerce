package com.rayaine.ecommerce.service;

import com.rayaine.ecommerce.model.Product;
import com.rayaine.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;



@Service
public class ProductService {

    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;

    }

    @PreAuthorize("hasRole('ADMIN')")
    public void addProduct( String productName, String productDescription, double productPrice, Product.Status status) throws Exception {
            Product product = new Product();
            product.setProductName(productName);
            product.setProductDescription(productDescription);
            product.setProductPrice(productPrice);
            product.setStatus(status);
            productRepository.save(product);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct( Long productId ) throws Exception {
            Product product = productRepository.findById(productId).orElseThrow(
                    () -> new Exception("product not found")
            );
            productRepository.delete(product);
    }


    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void changePrice( Long productId, double newPrice ) throws Exception {
            Product product = productRepository.findById(productId).orElseThrow(
                    () -> new Exception("product not found")
            );
            product.setProductPrice(newPrice);
            productRepository.save(product);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void changeStatus( Long productId, Product.Status newStatus ) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new Exception("product not found")
        );
        product.setStatus(newStatus);
        productRepository.save(product);
    }


    public Page<Product> getCatalogue(Product.Status status, Double minPrice , Double maxPrice , Pageable pageable){
        Specification<Product> specification = ( root, query,criteriaBuilder ) -> criteriaBuilder.conjunction();
        if( status != null ){
            specification = specification.and((root,query,cb)->cb.equal(root.get("status"),status));
        }
        if( minPrice != null ){
            specification = specification.and((root,query,cb)->cb.greaterThanOrEqualTo(root.get("productPrice"),minPrice));
        }
        if( maxPrice != null ){
            specification = specification.and((root,query,cb)->cb.lessThanOrEqualTo(root.get("productPrice"),maxPrice));
        }
        return productRepository.findAll(specification,pageable);
    }


}
