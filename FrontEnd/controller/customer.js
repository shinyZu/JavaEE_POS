$("#btnSaveCustomer").click(function (e) {
    console.log("inside addCustomer method....");

    $.ajax({
        url: "customer",
        method: "POST",
        data: $("#customerForm").serialize(),
        success: function (resp) {
            console.log(resp);
            alert(resp);
        },
        error: function (ob, textStatus, error) {
            alert(textStatus);
            console.log(ob);
        }
    })
})

