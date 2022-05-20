let rowSelected;
let updatedRow;

let input;
let newRow;

let searchValue;
let response;

let color;

let alertText;
let alertTitle;
let alertIcon;

getCustomerCount();
getItemCount();
getOrderCount();

function getCustomerCount() {
    $.ajax({
        url: "http://localhost:8080/pos/customer?option=GET_COUNT",
        method: "GET",
        async: false,
        success: function (resp) {
            $("#totalCustomers").text("0" + resp.data);
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

function getItemCount() {
    $.ajax({
        url: "http://localhost:8080/pos/item?option=GET_COUNT",
        method: "GET",
        async: false,
        success: function (resp) {
            $("#totalItems").text("0" + resp.data);
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

function getOrderCount() {
    $.ajax({
        url: "http://localhost:8080/pos/orders?option=GET_COUNT",
        method: "GET",
        async: false,
        success: function (resp) {
            $("#totalOrders").text("0" + resp.data);
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

function generateNextCustomerID() {
    $.ajax({
        url: "http://localhost:8080/pos/customer?option=LAST_ID",
        method: "GET",
        async: false,
        success: function (resp) {
            let lastCustId = resp.data;

            if (lastCustId === "null") {
                txtCustomerId.val("C00-001");
                return;
            }
            let nextCustId = ++lastCustId.split("-")[1];

            if (nextCustId <= 9) {
                nextCustId = "C00-00" + nextCustId;
                txtCustomerId.val(nextCustId);
                return nextCustId;

            } else if (nextCustId > 9) {
                nextCustId = "C00-0" + nextCustId;
                txtCustomerId.val(nextCustId);
                return nextCustId;

            } else if (nextCustId < 100) {
                nextCustId = "C00-" + nextCustId;
                txtCustomerId.val(nextCustId);
                return nextCustId;
            }
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

function generateNextItemCode() {
    $.ajax({
        url: "http://localhost:8080/pos/item?option=LAST_CODE",
        method: "GET",
        async: false,
        success: function (resp) {
            let lastItemCode = resp.data;

            if (lastItemCode === "null") {
                txtItemCode.val("I00-001");
                return;
            }
            let nextItemCode = ++lastItemCode.split("-")[1];

            if (nextItemCode <= 9) {
                nextItemCode = "I00-00" + nextItemCode;
                txtItemCode.val(nextItemCode);
                return nextItemCode;

            } else if (nextItemCode > 9) {
                nextItemCode = "I00-0" + nextItemCode;
                txtItemCode.val(nextItemCode);
                return nextItemCode;

            } else if (nextItemCode < 100) {
                nextItemCode = "I00-" + nextItemCode;
                txtItemCode.val(nextItemCode);
                return nextItemCode;
            }
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

function generateNextOrderID() {
    $.ajax({
        url: "http://localhost:8080/pos/orders?option=LAST_ID",
        method: "GET",
        async: false,
        success: function (resp) {
            let lastOrderId = resp.data;

            if (lastOrderId === "null") {
                orderId.val("OID-001");
                return;
            }
            let nextOrderId = ++lastOrderId.split("-")[1];

            if (nextOrderId <= 9) {
                nextOrderId = "OID-00" + nextOrderId;
                orderId.val(nextOrderId);
                return nextOrderId;

            } else if (nextOrderId > 9) {
                nextOrderId = "OID-0" + nextOrderId;
                orderId.val(nextOrderId);
                return nextOrderId;

            } else if (nextOrderId < 100) {
                nextOrderId = "OID-" + nextOrderId;
                orderId.val(nextOrderId);
                return nextOrderId;
            }
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

(function () {
    // (async () => {

    //     const { value: email } = await Swal.fire({
    //         title: 'Input email address',
    //         input: 'email',
    //         inputLabel: 'Your email address',
    //         inputPlaceholder: 'Enter your email address'
    //     })

    //     if (email) {
    //         Swal.fire(`Entered email: ${email}`)
    //     }

    // })()
    let email;
    let pwd;

    (async () => {
        const { value: formValues } = await Swal.fire({
            title: 'Login / Sign Up',
            html:
                '<label>Email Address</label>' +
                '<input id="swal-input1" class="swal2-input" type="email" size=25>' +
                '<label>Password</label>' +
                '<input id="swal-input2" class="swal2-input" type="password" size=21>',

            allowOutsideClick: false,

            confirmButtonText: 'Login',
            confirmButtonColor: '#1abc9c',

            showCancelButton: true,
            cancelButtonText: 'Sign Up',
            cancelButtonColor: '#ff7f50',

            // showDenyButton: true,
            // denyButtonText: 'Sign Up',
            // denyButtonColor: '#ff7f50',

            customClass: {
                cancelButton: 'order-1 right-gap',
                confirmButton: 'order-2',
            },
            // focusConfirm: false,
            allowEnterKey: true,
            // returnFocus: false,
            closeModal: true,
            // showCloseButton: true,
            preConfirm: () => {
                // return [
                // document.getElementById('swal-input1').value,
                // document.getElementById('swal-input2').value
                email = $('#swal-input1').val();
                pwd = $('#swal-input2').val();
                console.log(email);
                console.log(pwd);
                // ]
            }
        })
        // If login

        // Swal.fire({
        //     icon: 'error',
        //     title: 'Oops1...',
        //     showConfirmButton: false,
        //     text: 'Access Denied!',
        //     footer: '<a href="">Try Again</a>'
        // })

        if (email && pwd) {
            Swal.fire(`Email : ` + email + '\nPassword : ' + pwd)

        } else if (email) {
            Swal.fire({
                icon: 'error',
                title: 'Please enter password..',
                showConfirmButton: false,
                text: 'Access Denied!',
                footer: '<a href="">Try Again</a>'
            })

        } else if (pwd) {
            Swal.fire({
                icon: 'error',
                title: 'Please enter email..',
                showConfirmButton: false,
                text: 'Access Denied!',
                footer: '<a href="">Try Again</a>'
            })

        } else if (Swal.DismissReason.cancel) { // If Sign Up

            email = $('#swal-input1').val();
            pwd = $('#swal-input2').val();
            console.log(email);
            console.log(pwd);

            if (email && pwd) {
                Swal.fire(`S Email : ` + email + '\n S Password : ' + pwd)

            } else if (email) {
                Swal.fire({
                    icon: 'error',
                    title: 'Please enter password..',
                    showConfirmButton: false,
                    text: 'Access Denied!',
                    footer: '<a href="">Try Again</a>'
                })
                // Swal.fire(JSON.stringify(email))

            } else if (pwd) {
                Swal.fire({
                    icon: 'error',
                    title: 'Please enter email..',
                    showConfirmButton: false,
                    text: 'Access Denied!',
                    footer: '<a href="">Try Again</a>'
                })
            }
        }




        // ).then((result) => {
        //     if (result.isConfirmed) { // Login
        //         // Swal.fire(
        //         //     'Login'
        //         // )


        //         // if (email) {
        //         //     Swal.fire(`Entered email: ${email}`)

        //         // } else if (pwd) {
        //         //     Swal.fire(`Entered password: ${pwd}`)

        //         // }

        //         // Swal.fire({
        //         //     icon: 'error',
        //         //     title: 'Oops...',
        //         //     text: 'Something went wrong!',
        //         //     footer: '<a href="">Try Again</a>'
        //         // })

        //     } else { // Sign Up
        //         // Swal.fire(
        //         //     'Sign Up'
        //         // )
        //         Swal.fire('Something went wrong!', '<a href="">Try Again</a>', 'question')
        //     }
        // })
    })()
})();

$("#nav-home").click(function () {
    $("title").text("Home");

    $("#home-main").css('display', 'block');
    $("#customer-main").css('display', 'none');
    $("#store-main").css('display', 'none');
    $("#orders-main").css('display', 'none');

    $("#nav-home a").addClass("active");
    $("#nav-customer a").removeClass("active");
    $("#nav-store a").removeClass("active");
    $("#nav-orders a").removeClass("active");

    getCustomerCount();
    getItemCount();
    getOrderCount();
});

$("#nav-customer").click(function () {
    $("title").text("Customers");

    $("#home-main").css('display', 'none');
    $("#customer-main").css('display', 'block');
    $("#store-main").css('display', 'none');
    $("#orders-main").css('display', 'none');

    $("#nav-home a").removeClass("active");
    $("#nav-customer a").addClass("active");
    $("#nav-store a").removeClass("active");
    $("#nav-orders a").removeClass("active");

    txtCustomerId.attr("disabled", "disabled");
    $("#txtCustomerName").focus();

    loadAllCustomers();
    generateNextCustomerID();

});

$("#nav-store").click(function () {
    $("title").text("Store");

    $("#home-main").css('display', 'none');
    $("#customer-main").css('display', 'none');
    $("#store-main").css('display', 'block');
    $("#orders-main").css('display', 'none');

    $("#nav-home a").removeClass("active");
    $("#nav-customer a").removeClass("active");
    $("#nav-store a").addClass("active");
    $("#nav-orders a").removeClass("active");

    txtItemCode.attr("disabled", "disabled");
    $("#txtDescription").focus();

    loadAllItems();
    generateNextItemCode();
});

$("#nav-orders").click(function () {
    $("title").text("Orders");

    $("#home-main").css('display', 'none');
    $("#customer-main").css('display', 'none');
    $("#store-main").css('display', 'none');
    $("#orders-main").css('display', 'block');

    $("#nav-home a").removeClass("active");
    $("#nav-customer a").removeClass("active");
    $("#nav-store a").removeClass("active");
    $("#nav-orders a").addClass("active");

    generateNextOrderID();
    load_TblCustomerOrder();

    loadCmbCustomerId();
    loadCmbCustomerName();

    loadCmbItemCode();
    loadCmbDescription();

    select_OrderDetailRow();
});

// toastr.options = {
//   "closeButton": false,
//   "debug": true,
//   "newestOnTop": false,
//   "progressBar": false,
//   "positionClass": "toast-top-right",
//   "preventDuplicates": false,
//   "onclick": null,
//   "showDuration": "300",
//   "hideDuration": "1000",
//   "timeOut": "5000",
//   "extendedTimeOut": "1000",
//   "showEasing": "swing",
//   "hideEasing": "linear",
//   "showMethod": "fadeIn",
//   "hideMethod": "fadeOut"
// }

function isBorderGreen(inputField) {
    color = $(inputField).css('border-color');

    // if (color === "rgb(38, 222, 129)") {
    if (color === "rgb(39, 174, 96)") {
        return true;
    }
    return false;
}

function changeBorderColor(inputStatus, inputField) {
    switch (true) {
        case inputStatus === "valid":
            // $(inputField).css('border', '5px solid #26de81');
            $(inputField).css('border', '5px solid #27ae60');
            break;

        case inputStatus === "invalid":
            // $(inputField).css('border', '5px solid #ff3f34');
            $(inputField).css('border', '5px solid #e74c3c');
            break;

        default:
            $(inputField).css('border', '2px solid rgb(206, 212, 218)');
            break;
    }
}

function disableButton(btn) {
    $(btn).attr("disabled", "disabled");
}

function enableButton(btn) {
    $(btn).removeAttr("disabled");
}

function display_Alert(alertTitle, alertText, alertIcon) {

    if (alertTitle == "") {
        swal({
            text: alertText,
            icon: alertIcon,
            buttons: "OK",
            closeModal: true,
            closeOnClickOutside: false,
        });

    } else {
        swal({
            title: alertTitle,
            text: alertText,
            icon: alertIcon,
            buttons: "OK",
            closeModal: true,
            closeOnClickOutside: false,
        });
    }

}
