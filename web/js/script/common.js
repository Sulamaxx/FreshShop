document.addEventListener("DOMContentLoaded", async() => {


    const response = await fetch("CheckLogin");

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        if (json.success) {
            document.getElementById("login-after").classList.remove("d-none");
            document.getElementById("login-after").classList.add("d-block");
            document.getElementById("login-before").classList.add("d-none");
            document.getElementById("user-name").innerHTML=json.user.first_name;
        } else {
            document.getElementById("login-before").classList.remove("d-none");
            document.getElementById("login-before").classList.add("d-block");
            document.getElementById("login-after").classList.add("d-none");
        }

    } else {

        console.log("Error");

    }



});
