const popup = Notification();

var subCategoryList;
const loadDataForRegistration = async() => {

    const response = await fetch("LoadDataForRegistation");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {

            //load category
            loadOption("category", json.categoryList);

            subCategoryList = json.subCategoryList;

            //load units
            loadOption("unit", json.unitList);

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

const loadOption = (id, resultList) => {
    resultList.forEach(item => {
        let option = document.createElement("option");
        option.innerHTML = item.name;
        option.value = item.id;
        document.getElementById(id).appendChild(option);
    });
};

const loadSubCategory = () => {
    let sub_category = document.getElementById("sub-category");
    sub_category.length = 1;

    let selectedCategory = document.getElementById("category").value;

    const data = [];
    subCategoryList.forEach(item => {
        if (item.category.id == selectedCategory) {
            data.push(item);
        }
    });
    //load sub category
    loadOption("sub-category", data);



};

const registerProduct = async () => {

    const product = new FormData();


    product.append("description", document.getElementById("description").value);
    product.append("title", document.getElementById("title").value);
    product.append("qty", document.getElementById("qty").value);
    product.append("price", document.getElementById("price").value);
    product.append("discount", document.getElementById("discount").value);
    product.append("category", document.getElementById("category").value);
    product.append("sub_category", document.getElementById("sub-category").value);
    product.append("unit", document.getElementById("unit").value);
    product.append("image1", document.getElementById("image1").files[0]);
    product.append("image2", document.getElementById("image2").files[0]);
    product.append("image3", document.getElementById("image3").files[0]);

    const response = await fetch("ProductRegistration", {
        method: "POST",
        body: product

    });

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {

            popup.success({
                message: json.message
            });
            
            document.getElementById("title").value = "";
            document.getElementById("description").value = "";
            document.getElementById("qty").value = "";
            document.getElementById("price").value = "";
            document.getElementById("discount").value = "";
            document.getElementById("category").value = 0;
            document.getElementById("sub-category").value = 0;
            document.getElementById("unit").value = 0;
            document.getElementById("image1").value = "";
            document.getElementById("image2").value = "";
            document.getElementById("image3").value = "";
            
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