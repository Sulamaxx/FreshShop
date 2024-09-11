const popup = Notification();
const resendCode = async() => {

    const response = await fetch("ResendVerificationCode");

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

const SubmitVerificationCode = async () => {
    document.getElementById("verificationButton").innerHTML = "Waiting..";
    document.getElementById("verificationButton").disabled = true;
    let code = document.getElementById("verification_code").value;

    const response = await fetch("UserVerification?code=" + code);

    if (response.ok) {
        const json = await response.json();
        if (json.success) {
            popup.success({
                message: json.message
            });

            setTimeout(() => {
                window.location = "index.html";
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
    document.getElementById("verificationButton").innerHTML = "Submit";
    document.getElementById("verificationButton").disabled = false;

};
