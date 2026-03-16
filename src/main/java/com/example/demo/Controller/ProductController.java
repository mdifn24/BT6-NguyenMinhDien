package com.example.demo.Controller;

import com.example.demo.Model.Product;
import com.example.demo.Service.CategoryService;
import com.example.demo.Service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.nio.file.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;

    private final String UPLOAD_DIR = "./user-photos/";

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.getAll());
        return "products";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("product") Product product, BindingResult result,
                         @RequestParam("imageFile") MultipartFile imageFile, Model model) throws Exception {

        // 1. Gọi hàm upload và kiểm tra độ dài tên ảnh
        handleFileUpload(product, imageFile, result);

        // 2. Kiểm tra lại toàn bộ lỗi (bao gồm lỗi tên ảnh > 200 vừa check ở trên)
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "create";
        }

        productService.add(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("product", productService.getById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "edit";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("product") Product product, BindingResult result,
                         @RequestParam("imageFile") MultipartFile imageFile, Model model) throws Exception {

        if (!imageFile.isEmpty()) {
            handleFileUpload(product, imageFile, result);
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "edit";
        }

        productService.update(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        productService.delete(id);
        return "redirect:/products";
    }

    private void handleFileUpload(Product product, MultipartFile file, BindingResult result) throws Exception {
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();

            // KIỂM TRA ĐỘ DÀI TÊN FILE TẠI ĐÂY
            if (fileName != null && fileName.length() > 200) {
                // "image" là tên thuộc tính trong class Product,
                // thông báo lỗi này sẽ hiển thị tại thẻ th:errors="*{image}"
                result.rejectValue("image", "error.product", "Tên hình ảnh không quá 200 kí tự");
                return;
            }

            Path path = Paths.get(UPLOAD_DIR);
            if (!Files.exists(path)) Files.createDirectories(path);

            try (InputStream is = file.getInputStream()) {
                Files.copy(is, path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                product.setImage(fileName);
            }
        }
    }
}