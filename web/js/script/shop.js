var selectedSubCategoty = 0;
const loadShopData = async() => {

    const response = await fetch("LoadShopData");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {

            let categoryList = json.categoryList;
            let subCategoryList = json.subCategoryList;
            let productList = json.productList;


            // grid product view
            let grid_view_container = document.getElementById("grid-view-container");
            let grid_view_item = document.getElementById("grid-view-item");
            grid_view_container.innerHTML = "";

            productList.forEach(item => {

                let grid_view_item_clone = grid_view_item.cloneNode(true);

                grid_view_item_clone.querySelector("#grid-image").src = "product-images/" + item.id + "/image1.png";
                grid_view_item_clone.querySelector("#grid-item-view").href = "single-product.html?id=" + item.id;
                grid_view_item_clone.querySelector("#grid-title").innerHTML = item.title;
                grid_view_item_clone.querySelector("#grid-price-max").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price);
                grid_view_item_clone.querySelector("#grid-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price - ((item.price / 100) * item.discount));
                grid_view_item_clone.querySelector("#grid-add-cart").addEventListener("click", () => {
                    addToCart(item.id, 1);
                });
                grid_view_container.appendChild(grid_view_item_clone);
            });


            //list product View
            let list_view_container = document.getElementById("list-view");
            let list_view_item = document.getElementById("list-view-item");
            list_view_container.innerHTML = "";
            productList.forEach(item => {

                let list_view_item_clone = list_view_item.cloneNode(true);

                list_view_item_clone.querySelector("#list-image").src = "product-images/" + item.id + "/image1.png";
                list_view_item_clone.querySelector("#list-title").innerHTML = item.title;
                list_view_item_clone.querySelector("#list-description").innerHTML = item.description;
                list_view_item_clone.querySelector("#list-item-view").href = "single-product.html?id=" + item.id;
                list_view_item_clone.querySelector("#list-price-max").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price);
                list_view_item_clone.querySelector("#list-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price - ((item.price / 100) * item.discount));
                list_view_item_clone.querySelector("#list-add-cart").addEventListener("click", () => {
                    addToCart(item.id, 1);
                });
                list_view_container.appendChild(list_view_item_clone);
            });



            let list_category_container = document.getElementById("list-group-men");
            let list_category = document.getElementById("category-item");


            list_category_container.innerHTML = "";

            categoryList.forEach((item, index) => {
                let list_category_clone = list_category.cloneNode(true);

                //unique id
                let uniqueCategoryId = `sub-men${index}`;
                let uniqueCollapseId = `collapse-${index}`;

                let categoryLink = list_category_clone.querySelector("a");
                list_category_clone.querySelector("#category-name").innerHTML = item.name;

                // Set the unique IDs for the collapse behavior
                categoryLink.href = `#${uniqueCategoryId}`;
                categoryLink.setAttribute("aria-controls", uniqueCategoryId);

                let collapseDiv = list_category_clone.querySelector(".collapse");
                collapseDiv.id = uniqueCategoryId;
                collapseDiv.setAttribute("data-parent", "#list-group-men");

                let sub_category_container = list_category_clone.querySelector("#sub-category-container");

                let sub_category_item = list_category_clone.querySelector("#sub-category-item");

                sub_category_container.innerHTML = "";

                let count = 0;

                subCategoryList.forEach(item1 => {
                    if (item1.category.id == item.id) {
                        count++;
                        let sub_category_item_clone = sub_category_item.cloneNode(true);

                        sub_category_item_clone.querySelector("#sub-category-name").innerHTML = item1.name;
                        sub_category_item_clone.querySelector("#subcategory-selector").addEventListener("change", (e) => {
                            selectedSubCategoty = item1.id;
                            console.log(selectedSubCategoty)
                        });

                        sub_category_container.appendChild(sub_category_item_clone);
                    }
                });
                list_category_clone.querySelector("#category-small").innerHTML = "(" + count + ")";

                list_category_container.appendChild(list_category_clone);

            });



        } else {
            popup.error({
                message: "Empty Product"
            });
        }

    } else {

        popup.error({
            message: "Try again later!"
        });

    }

};

const searchProduct = async() => {

    let min_price = $("#slider-range").slider("values", 0);
    let max_price = $("#slider-range").slider("values", 1);

    const data = {
        selectedSubCategoty: selectedSubCategoty,
        search: document.getElementById("search").value,
        basic: document.getElementById("price-order").value,
        min_price: min_price,
        max_price: max_price
    };

    const response = await fetch("SearchProduct", {
        method: "POST",
        body: JSON.stringify(data),
        header: {
            "Content=Type": "application/json"
        }

    });

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.success) {

        } else {
            popup.error({
                message: json.message
            });
        }
    } else {
        console.log("Try again later!");
    }



};
