package admin_user.controller;


import admin_user.model.Brand;
import admin_user.model.Category;
import admin_user.service.container.BrandContainer;
import admin_user.service.BrandService;
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
@RequestMapping("/brand")

public class BrandController {
    @Autowired
    private BrandService brandService;


    @PostMapping(path = "/listing")
    @ResponseBody
    public BrandContainer postListing(@RequestBody ServerSideRequest ssr) {
        // JSON response: https://datatables.net/manual/server-side#Returned-data
        return brandService.getListing(ssr);
    }

    @GetMapping("/brand-list")
    public @ResponseBody Object getAllBrand(Model model) {
        List<Brand> brand = brandService.getAllBrands();
        model.addAttribute("brand", brand);
        return brand;
    }



    @GetMapping("/add")
    public String showAddBrandForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "brand/add";
    }

    @PostMapping("/add")
    public String addBrand(@ModelAttribute("brand") Brand brand) {
        brandService.saveBrand(brand);
        return "redirect:/pages/brand";
    }

//    @GetMapping("/edit/{id}")
//    public String showEditBrandForm(@PathVariable Long id, Model model) {
//        Brand brand = brandService.getBrandById(id);
//        model.addAttribute("brand", brand);
//        return "brand/edit";
//    }
//
//    @PostMapping("/edit/{id}")
//    public String editBrand(@PathVariable Long id, @ModelAttribute("brand") Brand brand) {
//        brandService.saveBrand(brand);
//        return "redirect:/brand";
//    }
@GetMapping(value = "/edit/{id}")
public @ResponseBody Object showEditCategoryForm(@PathVariable Long id) {
    Optional<Brand> brand = Optional.ofNullable(brandService.getBrandById(id));
    return brand;
}

    @PostMapping(value = "/edit/{id}")
    public String editCategory(@PathVariable Long id, @ModelAttribute("brand") Brand brand) {
        brand.setId(id); // Set the ID from path variable to the category object
        brandService.saveBrand(brand);
        return "redirect:/brand"; // Redirect to the category list page
    }













    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBrandById(@PathVariable Long id) {
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.ok("brand deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete category.");
        }
    }

}
