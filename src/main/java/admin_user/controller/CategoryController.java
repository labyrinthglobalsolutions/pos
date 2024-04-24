package admin_user.controller;

import admin_user.model.Category;
import admin_user.service.CategoryService;
import admin_user.service.container.ResponseContainer;
import admin_user.service.request.ServerSideRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    @PostMapping(path = "/listing")
    @ResponseBody
    public ResponseContainer postListing(@RequestBody ServerSideRequest ssr) {
        // JSON response: https://datatables.net/manual/server-side#Returned-data
        return categoryService.getListing(ssr);
    }

    @GetMapping("/category-list")
    public @ResponseBody Object getAllCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return categories;
    }

    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/add";
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute("category") Category category) {
        categoryService.saveCategory(category);
        return "redirect:/pages/categories";
    }
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/edit/{id}")
    public @ResponseBody Object showEditCategoryForm(@PathVariable Long id) {
        Optional<Category> category = Optional.ofNullable(categoryService.getCategoryById(id));
        return category;
    }

    @PostMapping(value = "/edit/{id}")
    public String editCategory(@PathVariable Long id, @ModelAttribute("category") Category category) {
        category.setId(id); // Set the ID from path variable to the category object
        categoryService.saveCategory(category);
        return "redirect:/categories"; // Redirect to the category list page
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete category.");
        }
    }

}

