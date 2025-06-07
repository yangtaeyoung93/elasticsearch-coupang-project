package com.example.coupangapiserver.product;

import com.example.coupangapiserver.product.domain.Product;
import com.example.coupangapiserver.product.domain.ProductDocument;
import com.example.coupangapiserver.product.dto.CreateProductRequestDto;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductDocumentRepository productDocumentRepository;


  public ProductService(ProductRepository productRepository, ProductDocumentRepository productDocumentRepository) {
    this.productRepository = productRepository;
    this.productDocumentRepository = productDocumentRepository;
  }

  public List<Product> getProducts(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    return productRepository.findAll(pageable).getContent();
  }

  public Product createProduct(CreateProductRequestDto createProductRequestDto) {
    // MySQL 등록
    Product product = new Product(
        createProductRequestDto.getName(),
        createProductRequestDto.getDescription(),
        createProductRequestDto.getPrice(),
        createProductRequestDto.getRating(),
        createProductRequestDto.getCategory()
    );
    Product savedProduct = productRepository.save(product);

    //Elasticsearch 등록
    ProductDocument productDocument = new ProductDocument(
            String.valueOf(savedProduct.getId()),
            savedProduct.getName(),
            savedProduct.getDescription(),
            savedProduct.getPrice(),
            savedProduct.getRating(),
            savedProduct.getCategory()
    );

    productDocumentRepository.save(productDocument);

    return savedProduct;
  }



  public void deleteProduct(Long id) {
    productRepository.deleteById(id); //MySQL 삭제
    productDocumentRepository.deleteById(String.valueOf(id)); //Elasticsearch 삭제
  }
}
