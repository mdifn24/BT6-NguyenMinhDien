package com.example.demo.Service;

import com.example.demo.Model.Product;
import com.example.demo.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service

public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAll() { return productRepository.findAll(); }
    public void add(Product product) { productRepository.save(product); }
    public Product getById(int id) { return productRepository.findById(id).orElse(null); }
    public void update(Product product) { productRepository.save(product); }
    public void delete(int id) { productRepository.deleteById(id); }
}