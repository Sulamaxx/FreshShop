const pop = Notification();
const Registration = async() => {
    let signUpButton = document.getElementById("sign-up-button");
    signUpButton.innerHTML = "Waiting...";
    signUpButton.disabled = true;

    const data = {
        first_name: document.getElementById("first_name").value,
        last_name: document.getElementById("last_name").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value
    };

    try {
        const response = await fetch("UserRegistration", {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json"
            }
        });

        const json = await response.json();

        if (response.ok && json.success) {
            pop.success({
                message: json.message
            });
            setTimeout(() => {
                window.location = "verification.html";
            }, 1000);
        } else {
            pop.error({
                message: json.message
            });
        }
    } catch (error) {
        console.error("Error:", error);
        pop.error({
            message: "Something went wrong!"
        });
    } finally {

        signUpButton.innerHTML = "Sign Up";
        signUpButton.disabled = false;
    }
};

