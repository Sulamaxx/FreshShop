const popup = Notification();

var selectedAddress = 0;

const loadCheckOut = async() => {
    const response = await fetch("LoadCheckOut");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {

            popup.success({
                message: json.message
            });

            let checkout_cart_container = document.getElementById("fs-checkout-cart-container");
            let checkout_cart_item = document.getElementById("fs-checkout-cart-item");

            checkout_cart_container.innerHTML = "";

            let cartList = json.cartList;
            let sub_total = 0;
            let total_discount = 0;

            // load shopping cart
            cartList.forEach(item => {

                let checkout_cart_item_clone = checkout_cart_item.cloneNode(true);
                checkout_cart_item_clone.querySelector("#fs-checkout-cart-item-title").innerHTML = item.product.title;
                checkout_cart_item_clone.querySelector("#fs-checkout-cart-item-title").href = "single-product.html?id=" + item.product.id;
                checkout_cart_item_clone.querySelector("#fs-checkout-cart-item-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.product.price);
                checkout_cart_item_clone.querySelector("#fs-checkout-cart-item-qty").innerHTML = item.qty;
                checkout_cart_item_clone.querySelector("#fs-checkout-cart-item-total").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.product.price * item.qty);

                checkout_cart_container.appendChild(checkout_cart_item_clone);

                sub_total += (item.product.price * item.qty);
                total_discount += (((item.product.price / 100) * item.product.discount) * item.qty);

            });

            document.getElementById("fs-cart-item-sub-total").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(sub_total);
            document.getElementById("fs-cart-item-discount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(total_discount);
            document.getElementById("fs-cart-item-tax").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format((sub_total / 100) * 5);
            document.getElementById("fs-cart-item-full-total").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(((sub_total / 100) * 5) + sub_total);

            //load exist address

            let address_container = document.getElementById("fs-address-container");
            let address_item = document.getElementById("fs-address-item");
            address_container.innerHTML = "";

            let addressList = json.addressList;

            if (addressList.length > 0) {
                addressList.forEach(item => {
                    let address_item_clone = address_item.cloneNode(true);

                    let checkbox = address_item_clone.querySelector("#fs-address-item-input");

                    checkbox.id = "exist-address-" + item.id;
                    address_item_clone.querySelector("#fs-address-item-name").setAttribute("for", checkbox.id);
                    address_item_clone.querySelector("#fs-address-item-name").innerHTML = item.line1 + ", " + item.line2 + ". " + item.postal_code + " - " + item.city.name + ", Name: " + item.first_name + " " + item.last_name + " - " + item.mobile + " - " + item.email;

                    checkbox.addEventListener("change", function () {
                        document.querySelectorAll('.custom-control-input').forEach(input => {
                            if (input !== checkbox) {
                                input.checked = false;
                            }
                            selectedAddress = item.id;
                            checkSelected();
                        });
                    });

                    address_container.appendChild(address_item_clone);
                });
            } else {
                let p = document.createElement("p");
                p.innerHTML = "New to checkout so continue with your new address, because you don't have any address record. Thank you";
                address_container.appendChild(p);
            }

            //city load
            loadOption("city", json.cityList);

        } else {
            popup.error({
                message: json.message
            });

            setTimeout(() => {
                window.location = "index.html"
            }, 1000);
        }

    } else {
        popup.error({
            message: "Try again later!"
        });
    }
};

const loadOption = (id, resultList) => {
    resultList.forEach(item => {
        let option = document.createElement("option");
        option.innerHTML = item.name;
        option.value = item.id;
        document.getElementById(id).appendChild(option);
    });
};

const checkSelected = () => {
    let checkboxes = document.querySelectorAll(".custom-control-input");
    let checked = false;

    checkboxes.forEach(checkbox => {
        if (checkbox.checked) {
            checked = true;
            document.getElementById("fs-address-new").classList.add("d-none");
            document.getElementById("fs-address-new").classList.remove("d-block");
        }
    });

    if (!checked) {
        document.getElementById("fs-address-new").classList.add("d-block");
        document.getElementById("fs-address-new").classList.remove("d-none");
        selectedAddress = 0;
    }
};


const checkOut = async() => {

    const data = {
        selectedAddress: selectedAddress,
        first_name: document.getElementById("first-name").value,
        last_name: document.getElementById("last-name").value,
        mobile: document.getElementById("mobile").value,
        email: document.getElementById("email").value,
        line1: document.getElementById("line1").value,
        line2: document.getElementById("line2").value,
        city: document.getElementById("city").value,
        postal_code: document.getElementById("postal-code").value
    };


    const response = await fetch("CheckOut", {
        method: "POST",
        body: JSON.stringify(data),
        header: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (json.success) {
            popup.success({
                message: json.message
            });
            setTimeout(() => {
                window.location = "index.html"
            }, 1000);
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