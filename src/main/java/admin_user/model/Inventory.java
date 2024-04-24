package admin_user.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product_id;

    private int quentity;
    private  int selling_price;
    private String batch_no;
    private int ip;
    private LocalTime startTime;
    private LocalDate startDate;

    private String productCode;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Product getProduct_id() {
        return product_id;
    }
    public void setProduct_id(Product product_id) {
        this.product_id = product_id;
    }
    public int getQuentity() {
        return quentity;
    }
    public void setQuentity(int quentity) {
        this.quentity = quentity;
    }
    public int getSelling_price() {
        return selling_price;
    }
    public void setSelling_price(int selling_price) {
        this.selling_price = selling_price;
    }
    public String getBatch_no() {
        return batch_no;
    }
    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }
    public int getIp() {
        return ip;
    }
    public void setIp(int ip) {
        this.ip = ip;
    }
    public LocalTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public String getProduct_name(){
        return product_id.getName();
    }

}
