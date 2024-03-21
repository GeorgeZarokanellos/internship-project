package gr.knowledge.internship.introduction.service;

import gr.knowledge.internship.introduction.dto.ProductDTO;
import gr.knowledge.internship.introduction.entity.Product;
import gr.knowledge.internship.introduction.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, ModelMapper modelMapper){
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    public List<ProductDTO> getProducts(){
        List<Product> productList = productRepository.findAll();
        return modelMapper.map(productList, new TypeToken<List<ProductDTO>>(){}.getType());
    }

    public ProductDTO getProductById(int id) {
        Product product = productRepository.getReferenceById(id);
        return modelMapper.map(product, new TypeToken<ProductDTO>(){}.getType());
    }
    public ProductDTO createProduct(ProductDTO productDTO){
        productRepository.save(modelMapper.map(productDTO, Product.class));
        return productDTO;
    }

    public ProductDTO updateProduct(int id, ProductDTO productDTO){
        productDTO.setId(id);
        productRepository.save(modelMapper.map(productDTO, Product.class));
        return  productDTO;
    }

    public boolean deleteProduct(int id){
        productRepository.deleteById(id);
        return true;
    }
}
