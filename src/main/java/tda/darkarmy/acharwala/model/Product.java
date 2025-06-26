package tda.darkarmy.acharwala.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category; // Achar, Papad and chutney
    private double price;
    private String brand;
    private String expiryDate;
    private String manufacturingDate;
    private String image;
    private String qrCode;
    private double amount;
    private boolean isAvailable;
    private int numberOfQuantities;
    private double discount;
}
