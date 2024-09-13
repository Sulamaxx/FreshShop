const popup = Notification();

const loadCartData = async() => {
    const response = await fetch("LoadCartData");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {
            popup.success({
                message: json.message
            });

            let cartList = json.cartList;

            let cart_container = document.getElementById("fs-cart-container");
            let cart_item = document.getElementById("fs-cart-item");
            cart_container.innerHTML = "";

            let sub_total = 0;
            let total_discount = 0;

            cartList.forEach(item => {
                let cart_item_clone = cart_item.cloneNode(true);

                cart_item_clone.querySelector("#fs-cart-item-image").src = "product-images/" + item.product.id + "/image1.png";
                cart_item_clone.querySelector("#fs-cart-item-view").href = "single-product.html?id=" + item.product.id;
                cart_item_clone.querySelector("#fs-cart-item-title").innerHTML = item.product.title;
                cart_item_clone.querySelector("#fs-cart-item-qty").value = item.qty;

                cart_item_clone.querySelector("#fs-cart-item-price-max").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.product.price);
                cart_item_clone.querySelector("#fs-cart-item-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.product.price - ((item.product.price / 100) * item.product.discount));
                cart_item_clone.querySelector("#fs-cart-item-total-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.product.price * item.qty);
                cart_container.appendChild(cart_item_clone);

                sub_total += (item.product.price * item.qty);
                total_discount += (((item.product.price / 100) * item.product.discount) * item.qty);

            });

            document.getElementById("fs-cart-item-sub-total").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(sub_total);
            document.getElementById("fs-cart-item-discount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(total_discount);
            document.getElementById("fs-cart-item-tax").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format((sub_total / 100) * 5);
            document.getElementById("fs-cart-item-full-total").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(((sub_total / 100) * 5) + sub_total);

        } else {
            popup.error({
                message: json.message
            });
        }

    } else {

        popup.error({
            message: "Try again later!"
        });

    }
};