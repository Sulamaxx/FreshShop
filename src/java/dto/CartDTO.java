package dto;

import entity.Product;

/**
 *
 * @author sjeew
 */
public class CartDTO {

    private Double qty;

    private Product product;

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
