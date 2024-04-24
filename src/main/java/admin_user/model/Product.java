package admin_user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String name;

    private String productCode;

    private double price;

    private int quantity;

    private double sellingPrice;
    private int hsn_no;
    private int gst_percent;
    private String gst_type;

    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    public int getHsn_no() {
        return hsn_no;
    }

    public void setHsn_no(int hsn_no) {
        this.hsn_no = hsn_no;
    }

    public int getGst_percent() {
        return gst_percent;
    }

    public void setGst_percent(int gst_percent) {
        this.gst_percent = gst_percent;
    }

    public String getGst_type() {
        return gst_type;
    }

    public void setGst_type(String gst_type) {
        this.gst_type = gst_type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String description;




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public String getCategory_Name(){
        return category.getCategoryName();
    }

    public String getBrand_name(){
        return brand.getBrandName();
    }

    public String getSubcategory_name(){
        return subcategory.getSubcategory_name();
    }

    public void setCategory(Category category) {
        this.category = category;
    }



}
