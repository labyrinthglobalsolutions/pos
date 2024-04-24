package admin_user.controller;


import admin_user.model.Brand;
import admin_user.model.Category;
import admin_user.model.Product;
import admin_user.model.Subcategory;
import admin_user.service.*;
import admin_user.service.container.ProductContainer;
import admin_user.service.request.ServerSideRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")

public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService; // Assuming you have a CategoryService

    @Autowired
    private SubCategoryService subcategoryService; // Assuming you have a SubcategoryService

    @Autowired
    private BrandService brandService; // Assuming you have a BrandService

    @GetMapping
    public String getAllProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "product/list";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        List<Subcategory> subcategories = subcategoryService.getAllSubcategories();
        List<Brand> brands = brandService.getAllBrands();

        model.addAttribute("product", new Product());
        model.addAttribute("categories", categories);
        model.addAttribute("subcategories", subcategories);
        model.addAttribute("brands", brands);

        return "product/add";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @PostMapping(path = "/listing")
    @ResponseBody
    public ProductContainer postListing(@RequestBody ServerSideRequest ssr) {
        // JSON response: https://datatables.net/manual/server-side#Returned-data
        return productService.getListing(ssr);
    }

    @GetMapping("/product-list")
    public @ResponseBody Object getAllProduct(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return products;
    }

    @GetMapping(value = "/edit/{id}")
    public @ResponseBody Object getByProductId(@PathVariable Long id) {
        Optional<Product> product = Optional.ofNullable(productService.getProductById(id));
        return product;
    }

    @GetMapping(value = "/get-product-by-code/{product_code}")
    public @ResponseBody Object getByProductCode(@PathVariable String product_code) {
        List<Product> product = productService.getProductByProductCode(product_code);
        return product;
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBrandById(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("product deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete category.");
        }
    }

//    @GetMapping("/view/{id}")
//    public String viewProduct(@PathVariable Long id, Model model) {
//        Product product = productService.getProductById(id);
//        model.addAttribute("product", product);
//        return "product/view";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String showEditProductForm(@PathVariable Long id, Model model) {
//        Product product = productService.getProductById(id);
//        List<Category> categories = categoryService.getAllCategories();
//        List<Subcategory> subcategories = subcategoryService.getAllSubcategories();
//        List<Product> products = brandService.getAllproduct();
//
//        model.addAttribute("product", product);
//        model.addAttribute("categories", categories);
//        model.addAttribute("subcategories", subcategories);
//
//
//        return "product/edit";
//    }
//
//    @PostMapping("/edit/{id}")
//    public String editProduct(@PathVariable Long id, @ModelAttribute("product") Product product) {
//        productService.saveProduct(product);
//        return "redirect:/products/view/" + id;
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteProduct(@PathVariable Long id) {
//        productService.deleteProduct(id);
//        return "redirect:/products";
//    }
//
//
//    @GetMapping("/getByProductCode")
//    public String getProductByProductCodeForm() {
//        return "product/getByProductCodeForm";
//    }
//
//    @PostMapping("/getByProductCode")
//    public String getProductByProductCode(@RequestParam("productCode") String productCode, Model model) {
//        Product product = productService.getProductByProductCode(productCode);
//
//        if (product != null) {
//            model.addAttribute("product", product);
//            return "product/viewByProductCode";
//        } else {
//            // Handle product not found
//            model.addAttribute("errorMessage", "Product not found for the given product code.");
//            return "product/getByProductCodeForm";
//        }
//    }



}
