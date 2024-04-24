package admin_user.controller;


import admin_user.model.Category;
import admin_user.model.Subcategory;
import admin_user.service.CategoryService;
import admin_user.service.SubCategoryService;
import admin_user.service.container.ResponseContainer;
import admin_user.service.container.SubcategoryContainer;
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
@RequestMapping("/subcategories")
public class SubCategoryController {

    @Autowired
    private SubCategoryService subcategoryService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping(path = "/listing")
    @ResponseBody
    public SubcategoryContainer postListing(@RequestBody ServerSideRequest ssr) {
        // JSON response: https://datatables.net/manual/server-side#Returned-data
        return subcategoryService.getListing(ssr);
    }



//    @GetMapping
//    public String getAllSubcategories(Model model) {
//        List<Subcategory> subcategories = subcategoryService.getAllSubcategories();
//        model.addAttribute("subcategories", subcategories);
//        return "subcategory/list";
//
//    }

    @GetMapping("/subcategory-list")
    public @ResponseBody Object getAllSubCategories(Model model) {
        List<Subcategory> subcategories = subcategoryService.getAllSubcategories();
        model.addAttribute("subcategories", subcategories);
        return subcategories;
    }

    @GetMapping("/add")
    public String showAddSubcategoryForm(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("subcategory", new Subcategory());
        model.addAttribute("categories", categories);
        return "subcategory/add";
    }

    @PostMapping("/add")
    public String addSubcategory(@ModelAttribute("subcategory") Subcategory subcategory) {
        subcategoryService.saveSubcategory(subcategory);
        return "redirect:/subcategories";
    }

    @GetMapping("/edit/{id}")
    public  @ResponseBody Object showEditSubcategoryForm(@PathVariable Long id) {
        Optional<Subcategory> subcategory = Optional.ofNullable(subcategoryService.getSubcategoryById(id));
        return subcategory;

    }
    @PostMapping("/edit/{id}")
    public String editSubcategory(@PathVariable Long id, @ModelAttribute("subcategory") Subcategory subcategory) {
        subcategoryService.saveSubcategory(subcategory);
        return "redirect:/subcategories";
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable Long id) {
        try {
            subcategoryService.deleteSubcategory(id);
            return ResponseEntity.ok("SubCategory deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete category.");
        }
    }








    }
