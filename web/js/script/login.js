const popup = Notification();
const Login = async() => {
    document.getElementById("loginButton").innerHTML = "Wait..";
    document.getElementById("loginButton").disabled = true;
    const user = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value
    };

    const response = await fetch("UserLogin", {
        method: "POST",
        body: JSON.stringify(user),
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

            if (json.message == "Login successfully") {
                setTimeout(() => {
                    window.location = "index.html";
                }, 1000);
            } else {
                setTimeout(() => {
                    window.location = "verification.html";
                }, 1000);
            }
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
    document.getElementById("loginButton").innerHTML = "Log In";
    document.getElementById("loginButton").disabled = false;

};