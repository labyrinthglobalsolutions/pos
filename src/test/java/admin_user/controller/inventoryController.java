package admin_user.controller;

import admin_user.model.Inventory;
import admin_user.model.InventoryMap;
import admin_user.service.InventoryService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

public class inventoryController {


    @Controller
    public class InventoryController {
        @Autowired
        private InventoryService inventoryService;

        @GetMapping("/addInventory")
        public String showAddInventoryForm(Model model) {
            model.addAttribute("inventory", new Inventory());
            model.addAttribute("inventoryMap", new InventoryMap());
            return "addInventory";
        }

        @PostMapping("/addInventory")
        public String addInventory(@ModelAttribute("inventory") Inventory inventory) {
            inventoryService.addInventory(inventory);
            return "redirect:/";
        }
    }


}
