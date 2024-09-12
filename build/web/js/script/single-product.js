const popup = Notification();

var add_to_cart_qty_single_product = 1;

const loadSingleProduct = async() => {

    let parameters = new URLSearchParams(window.location.search);

    if (parameters.has("id")) {
        const response = await fetch("LoadSingleProduct?id=" + parameters.get("id"));

        if (response.ok) {
            const json = await response.json();

            if (json.success) {
                let product = json.product;
                document.getElementById("image1").src = "product-images/" + product.id + "/image1.png";
                document.getElementById("image2").src = "product-images/" + product.id + "/image2.png";
                document.getElementById("image3").src = "product-images/" + product.id + "/image3.png";

                document.getElementById("image1-small").src = "product-images/" + product.id + "/image1.png";
                document.getElementById("image2-small").src = "product-images/" + product.id + "/image2.png";
                document.getElementById("image3-small").src = "product-images/" + product.id + "/image3.png";


                document.getElementById("fs-product-title").innerHTML = product.title;
                document.getElementById("fs-product-description").innerHTML = product.description;
                document.getElementById("fs-product-category").innerHTML = product.subCategory.category.name;
                document.getElementById("fs-product-sub-category").innerHTML = product.subCategory.name;
                document.getElementById("fs-product-qty").innerHTML = product.qty + " " + product.unit.name;
                document.getElementById("fs-product-price-max").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(product.price);
                document.getElementById("fs-product-price-min").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(product.price - ((product.price / 100) * product.discount));

                document.getElementById("fs-product-add-cart-qty").addEventListener("change", (e) => {
                    add_to_cart_qty_single_product = e.target.value;
                    console.log(add_to_cart_qty_single_product);
                });
                document.getElementById("fs-product-add-cart-button").addEventListener("click", (event) => {
                    event.preventDefault();
                    addToCart(product.id, add_to_cart_qty_single_product);
                });

                let productList = json.productList;

                let similar_item_container = document.getElementById("fs-similar-container");
                let similar_item = document.getElementById("fs-similar-item");

                similar_item_container.innerHTML = "";

                productList.forEach(item => {
                    let similar_item_clone = similar_item.cloneNode(true);

                    similar_item_clone.querySelector("#fs-similar-item-title").innerHTML = item.title;
                    similar_item_clone.querySelector("#fs-similar-item-price-max").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price);
                    similar_item_clone.querySelector("#fs-similar-item-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price - ((item.price / 100) * item.discount));
                    similar_item_clone.querySelector("#fs-similar-item-image").src = "product-images/" + item.id + "/image1.png";
                    //view single product
                    similar_item_clone.querySelector("#fs-similar-item-view").href = "single-product.html?id=" + item.id;

                    //add to cart list
                    similar_item_clone.querySelector("#fs-similar-item-cart").addEventListener("click", (event) => {
                        event.preventDefault();
                        addToCart(item.id, 1);
                    });

                    //add to wish list
                    similar_item_clone.querySelector("#fs-similar-item-wishlist").addEventListener("click", (event) => {
                        event.preventDefault();
                        //need to set add to wishlist
                    });
                    similar_item_container.appendChild(similar_item_clone);
                });

                /* ..............................................
                 Featured Products
                 ................................................. */

                $('.featured-products-box').owlCarousel({
                    loop: true,
                    margin: 15,
                    dots: false,
                    autoplay: true,
                    autoplayTimeout: 3000,
                    autoplayHoverPause: true,
                    navText: ["<i class='fas fa-arrow-left'></i>", "<i class='fas fa-arrow-right'></i>"],
                    responsive: {
                        0: {
                            items: 1,
                            nav: true
                        },
                        600: {
                            items: 3,
                            nav: true
                        },
                        1000: {
                            items: 4,
                            nav: true,
                            loop: true
                        }
                    }
                });


            } else {
                popup.error({
                    message: json.message
                });
                setTimeout(() => {
                    window.location = "index.html";
                }, 1000);
            }

        } else {
            window.location = "index.html";
        }

    } else {

        window.location = "index.html";
    }




};

const addToCart = async(id, qty) => {

    const response = await fetch("AddToCart?id=" + id + "&qty=" + qty);

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {
            popup.success({
                message: json.message
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