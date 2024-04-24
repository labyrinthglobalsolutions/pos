package admin_user.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;



@Entity
@Table(name = "inventoriesmap")
public class InventoryMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long inventory_id;
    private int quentity;
    private LocalDate receiveing_date;
    private LocalTime receiveing_time;

    private String batch_no;

    private LocalTime startTime;
    private int ip;


    public void setId(Long id) {
        this.id = id;
    }

    public long getInventory_id() {
        return inventory_id;
    }

    public void setInventory_id(long inventory_id) {
        this.inventory_id = inventory_id;
    }

    public int getQuentity() {
        return quentity;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getReceiveing_date() {
        return receiveing_date;
    }

    public void setReceiveing_date(LocalDate receiveing_date) {
        this.receiveing_date = receiveing_date;
    }

    public LocalTime getReceiveing_time() {
        return receiveing_time;
    }

    public void setReceiveing_time(LocalTime receiveing_time) {
        this.receiveing_time = receiveing_time;
    }

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public int getIp() {
        return ip;
    }

    public void setIp(int ip) {
        this.ip = ip;
    }

    public void setQuentity(int quentity) {
        this.quentity = quentity;
    }
}








