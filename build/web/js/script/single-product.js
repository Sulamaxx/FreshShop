const popup = Notification();
const loadSingleProduct = async() => {

    let parameters = new URLSearchParams(window.location.search);

    if (parameters.has("id")) {
        const response = await fetch("LoadSingleProduct?id=" + parameters.get("id"));

        if (response.ok) {
            const json = await response.json();
            console.log(json);

            if (json.success) {
                let product = json.product;
                document.getElementById("image1").src = "product-images/" + product.id + "/image1.png";
                document.getElementById("image2").src = "product-images/" + product.id + "/image2.png";
                document.getElementById("image3").src = "product-images/" + product.id + "/image3.png";

                document.getElementById("image1-small").src = "product-images/" + product.id + "/image1.png";
                document.getElementById("image2-small").src = "product-images/" + product.id + "/image2.png";
                document.getElementById("image3-small").src = "product-images/" + product.id + "/image3.png";




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