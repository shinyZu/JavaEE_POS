console.log("loaded Index Controller...");

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

function getCustomerCount() {
    $.ajax({
        url:"customer?option=GET_COUNT",
        method:"GET",
        success:function (resp) {
            $("#totalCustomers").text("0" + resp.data);
        },
        error: function (ob, textStatus, error) {
            alert(textStatus);
            console.log(ob);
        }
    });
}

function generateNextCustomerID() {
    $.ajax({
        url:"customer?option=LAST_ID",
        method:"GET",
        success:function (resp) {
            let lastCustId = resp.data;
            console.log(lastCustId);

            let nextCustId = ++lastCustId.split("-")[1];
            console.log(nextCustId);

            if (nextCustId < 9) {
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

$("#nav-home").click(function () {
    console.log("inside Home Tab..");

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
});

$("#nav-customer").click(function () {
    console.log("inside Customer Tab..");

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
    // $("#txtCustomerId").focus();

    loadAllCustomers();
    generateNextCustomerID();

});

$("#nav-store").click(function () {
    console.log("inside Store Tab..");
    $("title").text("Store");

    $("#home-main").css('display', 'none');
    $("#customer-main").css('display', 'none');
    $("#store-main").css('display', 'block');
    $("#orders-main").css('display', 'none');

    $("#nav-home a").removeClass("active");
    $("#nav-customer a").removeClass("active");
    $("#nav-store a").addClass("active");
    $("#nav-orders a").removeClass("active");

    $("#txtItemCode").focus();
});

$("#nav-orders").click(function () {
    console.log("inside Orders Tab..");
    $("title").text("Orders");

    $("#home-main").css('display', 'none');
    $("#customer-main").css('display', 'none');
    $("#store-main").css('display', 'none');
    $("#orders-main").css('display', 'block');

    $("#nav-home a").removeClass("active");
    $("#nav-customer a").removeClass("active");
    $("#nav-store a").removeClass("active");
    $("#nav-orders a").addClass("active");

    // setComboBoxes();
    loadCmbCustomerId();
    loadCmbCustomerName();

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
