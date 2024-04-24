package admin_user.controller;

import admin_user.model.*;
import admin_user.service.*;
import admin_user.service.container.InventoryContainer;
import admin_user.service.request.ServerSideRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/addInventory")
    public String showAddInventoryForm(Model model) {
        model.addAttribute("inventory", new Inventory());
        model.addAttribute("inventoryMap", new InventoryMap());
        return "addInventory";
    }

    @PostMapping("/addInventory") // This method handles POST requests
    public String addInventory(@ModelAttribute("inventory") Inventory inventory) {

        long inventory_id = inventoryService.addInventory(inventory);
        inventoryService.addInventoryMap(inventory_id, inventory);
        return "redirect:/addInventory"; // Redirect to the form after insertion
    }

    @PostMapping(path = "/listing")
    @ResponseBody
    public InventoryContainer postListing(@RequestBody ServerSideRequest ssr) {
        return inventoryService.getListing(ssr);
    }

    @GetMapping("/inventory-list")
    public @ResponseBody Object getAllInventory(Model model) {
        List<Inventory> inventories= InventoryService.getAllInventory();
        model.addAttribute("inventory", inventories);
        return inventories;
    }

}
