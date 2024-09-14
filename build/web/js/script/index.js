const popup = Notification();
const loadIndexData = async() => {

    const response = await fetch("LoadIndexData");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {
            popup.success({
                message: json.message
            });

            // grid product view
            let product_container = document.getElementById("product-container");
            let product_item = document.getElementById("product-item");
            product_container.innerHTML = "";

            json.productList.forEach(item => {

                let product_item_clone = product_item.cloneNode(true);

                product_item_clone.querySelector("#product-image").src = "product-images/" + item.id + "/image1.png";
                product_item_clone.querySelector("#product-item-view").href = "single-product.html?id=" + item.id;
                product_item_clone.querySelector("#product-title").innerHTML = item.title;
                product_item_clone.querySelector("#product-price-max").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price);
                product_item_clone.querySelector("#product-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price - ((item.price / 100) * item.discount));
                product_item_clone.querySelector("#product-add-cart").addEventListener("click", () => {
                    addToCart(item.id, 1);
                });
                product_container.appendChild(product_item_clone);
            });


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