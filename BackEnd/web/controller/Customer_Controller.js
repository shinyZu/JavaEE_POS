let customerId;
let customerName;
let customerAddress;
let customerContact;

let txtCustomerId = $("#txtCustomerId");
let txtCustomerName = $("#txtCustomerName");
let txtAddress = $("#txtAddress");
let txtContact = $("#txtContact");

let txtSearchId = $("#txtSearchCustomer");

let nextID;
let lastId;



disableButton(".btnSaveCustomer");
disableButton("#btnEditCustomer");
disableButton("#btnDeleteCustomer");

console.log("Inside Customer Controller...");


/* ---------------Initially Hide the Error Indicators----------*/

$("#customerForm p.errorText").hide();

/* -----------------------------------------------------------------CRUD Operation---------------------------------------------------*/

function addCustomer() {
    customerId = txtCustomerId.val();
    customerName = txtCustomerName.val();
    customerAddress = txtAddress.val();
    customerContact = txtContact.val();

    console.log(customerId,customerName,customerAddress,customerContact);
    txtCustomerId.removeAttr("disabled");

    $.ajax({
        url: "customer",
        method: "POST",
        data: $("#customerForm").serialize(),
        success: function (resp) {
            if (resp.status == 200) {
                console.log(resp);
                toastr.success(resp.message);
                loadAllCustomers();
                getCustomerCount();
                generateNextCustomerID();
                reset_CustomerForm();

            } else {
                toastr.error(resp.data);
            }
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

function updateCustomer() {
    let custObj = {
        id: txtCustomerId.val(),
        name: txtCustomerName.val(),
        address: txtAddress.val(),
        contact: txtContact.val()
    }

    $.ajax({
        url: "customer",
        method: "PUT",
        contentType: "application/json",
        data: JSON.stringify(custObj),
        success: function (resp) {
            if (resp.status == 200) {
                console.log(resp.message);
                toastr.success(resp.message);

                loadAllCustomers();
                generateNextCustomerID();

                loadCmbCustomerId();
                loadCmbCustomerName();

                clearCustomerFields();
                load_TblCustomerOrder();
                select_OrderDetailRow();

            } else if (resp.status == 400) {
                toastr.error(resp.message);
            } else {
                toastr.error(resp.message);
            }
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

function deleteCustomer(row) {
    customerId = $(row).children(':nth-child(1)').text();

    swal({
        title: 'Are you sure you want to delete this Customer..?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        buttons: ["Cancel", "Delete"],
        dangerMode: true,
        closeModal: true,
        closeOnClickOutside: false,

    }).then(result => {

        if (result) {

            $.ajax({
                url: "customer?customerID=" + customerId,
                method: "DELETE",
                success: function (resp) {
                    // console.log(resp);
                    if (resp.status == 200) {
                        // alert(resp.message);
                        swal({
                            title: 'Deleted!',
                            text: "Customer  " + customerId + "  Deleted.",
                            icon: 'success',
                            buttons: ["OK"],
                            timer: 2000,
                            closeModal: true,
                        });

                        loadAllCustomers();
                        getCustomerCount();

                        $("#tblOrders-body").empty();
                        load_TblCustomerOrder();
                        $("#totalOrders").text("0" + ordersDB.length);

                        generateNextCustomerID();
                        generateNextOrderID();
                        reset_CustomerForm();

                        select_OrderDetailRow();
                        clearInvoiceFields();
                        clearInvoiceTable();

                    } else if (resp.status == 400) {
                        toastr.error(resp.message);
                    } else {
                        toastr.error(resp.message);
                    }

                },
                error: function (ob, status, t) {
                    console.log(ob);
                }
            });

            /*swal({
                title: 'Deleted!',
                text: "Customer  " + customerId + "  Deleted.",
                icon: 'success',
                buttons: ["OK"],
                timer: 2000,
                closeModal: true,
            });

            for (let i in customerDB) {
                if (customerDB[i].getCustomerID() == customerId) {
                    customerDB.splice(i, 1);
                }
            }

            $(row).remove();

            $("#totalCustomers").text("0" + customerDB.length);

            for (let i = 0; i < ordersDB.length; i++) {
                if (customerId == ordersDB[i].getCustomerID()) {
                    ordersDB.splice(i, 1);
                    i--;
                }

            }*/

            // for (let i in ordersDB) {
            //     if (customerId == ordersDB[i].getCustomerID()) {
            //         // console.log(i);
            //         ordersDB.splice(i,1);
            //         i--;
            //     }
            // }  

            /*$("#tblOrders-body").empty();
            load_TblCustomerOrder();
            $("#totalOrders").text("0" + ordersDB.length);

            generateNextOrderID();
            reset_CustomerForm();

            select_OrderDetailRow();
            clearInvoiceFields();
            clearInvoiceTable();*/
        }
    })

    // if (window.confirm("Do you really need to delete this Customer..?")) {

    //     for (let i in customerDB) {
    //         if (customerDB[i].getCustomerID() == customerId) {
    //             customerDB.splice(i,1);
    //         }
    //     }  
    //     $(row).remove();
    //     reset_CustomerForm();
    // }

    loadCmbCustomerId();
    loadCmbCustomerName();
    clearCustomerFields();

}

function loadAllCustomers() {
    console.log("inside loadAllCustomers");
    $("#tblCustomer-body").empty();

    $.ajax({
        url: "customer?option=GETALL",
        method: "GET",
        success: function (resp) {
            console.log(resp);
            // alert(resp.message);

            for (let c of resp.data) {
                let customer = new Customer(c.id, c.name, c.address, c.contact);

                newRow = `<tr>
                    <td>${customer.getCustomerID()}</td>
                    <td>${customer.getCustomerName()}</td>
                    <td>${customer.getCustomerAddress()}</td>
                    <td>0${customer.getCustomerContact()}</td>
                </tr>`;

                $("#tblCustomer-body").append(newRow);
            }
            clearCustomerFields();
            select_CustomerRow();
        },

        error: function (ob, textStatus, error) {
            alert(textStatus);
            console.log(ob);
        }
    });
}

function searchCustomer(searchValue) {
    console.log("inside search Customer");
    console.log("searchValue : " + searchValue);

    $.ajax({
        url: "customer?option=SEARCH&customerID=" + searchValue,
        method: "GET",
        success: function (resp) {
            response = resp;
            let obj = resp.data;
            obj = new Customer(obj.id, obj.name, obj.address ,obj.contact);
            console.log(JSON.stringify(resp.data));

            if (JSON.stringify(resp.data) !== "{}") { // if resp.data = '{"id":"C00-005","name":"Ramal","address":"Jaffna","contact":"716455455"}'
                txtCustomerId.val(obj.getCustomerID());
                txtCustomerName.val(obj.getCustomerName());
                txtAddress.val(obj.getCustomerAddress());
                txtContact.val("0"+obj.getCustomerContact());

                validate_CustomerForm();
                return true;

            } else { // if resp.data = '{}'
                swal({
                    title: "Customer " + searchValue + " doesn't exist...",
                    text: "\n",
                    icon: 'warning',
                    buttons: false,
                    timer: 2000,
                    closeModal: true,
                });

                reset_CustomerForm();
                return false;
            }

        },

        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

/* ------------------Save Customer------------*/

/* When/after a new Customer is Saved:
    1. add Customer to table
    2. fill input fields when a row is selected
    3. delete the selected Customer from the table
*/

/*function isCustomerAlreadyExist() {
    response = customerDB.find(function (obj) {
        return obj.getCustomerID() == txtCustomerId.val();
    });
}*/

/*function checkDB_BeforeSaveCustomer() {

    nextID = txtCustomerId.val().split("-")[1];

    if (customerDB.length == 0) {
        lastId = "C00-000";
    } else {
        lastId = customerDB.slice(customerDB.length - 1, customerDB.length)[0].getCustomerID();
    }

    lastId = lastId.split("-")[1];

    if (response) { // if Customer already exist
        alertText = "A Customer already exists with ID : " + txtCustomerId.val() + "...";
        display_Alert("", alertText, "warning");

    } else if (nextID < lastId) {
        lastId++;

        if (lastId < 9) {
            alertText = "ID : " + txtCustomerId.val() + " is not available...\nPlease use ID : C00-00" + lastId;
            display_Alert("", alertText, "info");

        } else if (lastId >= 10) {
            alertText = "ID : " + txtCustomerId.val() + " is not available...\nPlease use ID : C00-0" + lastId;
            display_Alert("", alertText, "info");
        }

    } else if (nextID > ++lastId) {

        if (lastId < 9) {
            alertTitle = "C00-00" + lastId;
            alertText = "is the next Available Customer ID";
            display_Alert(alertTitle, alertText, "info");

        } else if (lastId >= 10) {
            alertTitle = "C00-0" + lastId;
            alertText = "is the next Available Customer ID";
            display_Alert(alertTitle, alertText, "info");
        }


    } else {
        Swal.fire({
            text: "Are you sure you want to Save this Customer..?",
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Save',
            confirmButtonColor: '#1abc9c',
            customClass: {
                cancelButton: 'order-1 right-gap',
                confirmButton: 'order-2',
            },
            allowOutsideClick: false,
            // allowEnterKey: false,
            // keydownListenerCapture: false,
            // stopKeydownPropagation: false,
            returnFocus: false,


        }).then(result => {
            if (result.isConfirmed) {
                addCustomer();
                reset_CustomerForm();
            }
            // return;
        });
    }
}*/

$(".btnSaveCustomer").click(function (e) {
    // isCustomerAlreadyExist();
    // checkDB_BeforeSaveCustomer();
    // select_CustomerRow();

    // $("#tblCustomer-body>tr").off("dblclick");
    // delete_CustomerRowOnDblClick();

    Swal.fire({
        text: "Are you sure you want to Save this Customer..?",
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Save',
        confirmButtonColor: '#1abc9c',
        customClass: {
            cancelButton: 'order-1 right-gap',
            confirmButton: 'order-2',
        },
        allowOutsideClick: false,
        // allowEnterKey: false,
        // keydownListenerCapture: false,
        // stopKeydownPropagation: false,
        returnFocus: false,


    }).then(result => {
        if (result.isConfirmed) {
            console.log("Save Button clicked....")
            addCustomer();
            reset_CustomerForm();

            // select_CustomerRow();
            $("#tblCustomer-body>tr").off("dblclick");
            delete_CustomerRowOnDblClick();

        }
        // return;
    });
});

/* ------------------Update Customer------------*/

$("#btnEditCustomer").click(function (e) {
    select_CustomerRow();

    Swal.fire({
        text: "Are you sure you want to Update this Customer..?",
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Update',
        confirmButtonColor: '#3498db',
        customClass: {
            cancelButton: 'order-1 right-gap',
            confirmButton: 'order-2',
        },
        allowOutsideClick: false,
        returnFocus: false,

    }).then(result => {
        if (result.isConfirmed) {
            updateCustomer();
            // loadAllCustomers(customerDB);
            reset_CustomerForm();

            // select_CustomerRow();
            $("#tblCustomer-body>tr").off("dblclick");
            delete_CustomerRowOnDblClick();
        }
    });
});

/* ------------------Search Customer------------*/

$("#txtSearchCustomer").keyup(function (e) {
    // e.preventDefault();
    // if(e.key == "Enter") {
    //     $("#txtSearchCustomer").off("click");
    //     searchValue = txtSearchId.val();
    //     // searchCustomer(txtSearchId.val());

    //     if (!searchCustomer(searchValue)) {
    //         alert("Customer "+ searchValue + " doesn't exist on ENTER...");
    //         reset_CustomerForm();
    //         $(txtSearchId).focus();
    //     }
    // }

    searchValue = $(this).val();

    $("#btnSearchCustomer").off("click");
    $("#btnSearchCustomer").click(function (e) {
        // e.preventDefault();
        searchCustomer(searchValue);

    });

    if (e.key == "Enter") {
        e.preventDefault();
        searchCustomer(searchValue);
    }

    $("#tblCustomer-body>tr").each(function () {
        let isFound = false;
        $(this).each(function () {  // search td of each tr one by one
            if ($(this).text().toLowerCase().indexOf(searchValue.toLowerCase()) >= 0) {
                isFound = true;
            }
        });
        if (isFound) {
            $(this).show();

        } else {
            $(this).hide();
        }
    });


});

/* -------------------------------------------------------------------Validation--------------------------------------------------- */

/* --------------------------Validate & Jump to Next Field On Enter---------------------------------*/

var regExCusID = /^(C00-)[0-9]{3,4}$/;
var regExCusName = /^[A-Z][a-z ]{4,9}[A-z]{1,10}$|^[A-Z][a-z ]{3,20}$/;
var regExCusAddress = /^[A-z0-9 \.]{5,}$/;
var regExCusContact = /^[0-9]{10}$/

function select_CustomerRow() {
    console.log("clicked a row.....");
    $("#tblCustomer-body>tr").click(function () {
        rowSelected = this;
        customerId = $(this).children(':nth-child(1)').text();

        searchCustomer(customerId);

        enableButton("#btnEditCustomer");
        enableButton("#btnDeleteCustomer");

        $("#btnDeleteCustomer").off("click");

        /* ------------------Delete Customer------------*/

        $("#btnDeleteCustomer").click(function () {
            deleteCustomer(rowSelected);
        });

        $("#tblCustomer-body>tr").off("dblclick");
        delete_CustomerRowOnDblClick();
    });

}

function delete_CustomerRowOnDblClick() {
    $("#tblCustomer-body>tr").dblclick(function () {
        rowSelected = $(this);
        deleteCustomer(rowSelected);
    });
}

function validate_CustomerID(input, txtField) {

    if (regExCusID.test(input)) {
        changeBorderColor("valid", txtField);

        // once the current input field is green change the the border of next input field to red
        if (!validate_CustomerName(txtCustomerName.val(), txtCustomerName)) {
            changeBorderColor("invalid", txtCustomerName);
            $("#customerForm p.errorText").eq(1).show();
            $("#errorName").text("*Required Field* Min 5, Max 20, Spaces Allowed");
        }

        $("#customerForm p.errorText").eq(0).hide();
        return true;

    } else {
        changeBorderColor("invalid", txtField);
        $("#customerForm p.errorText").eq(0).show();
        $("#errorID").text("*Required Field* Format : C00-000");

        disableButton(".btnSaveCustomer");
        disableButton("#btnEditCustomer");
        return false;
    }
}

function validate_CustomerName(input, txtField) {

    if (regExCusName.test(input)) {
        changeBorderColor("valid", txtField);

        // once the current input field is green change the the border of next input field to red
        if (!validate_CustomerAddress(txtAddress.val(), txtAddress)) {
            changeBorderColor("invalid", txtAddress);
            $("#customerForm p.errorText").eq(2).show();
            $("#errorAddress").text("*Required Field* Minimum 5");
        }

        $("#customerForm p.errorText").eq(1).hide();
        return true;

    } else {
        changeBorderColor("invalid", txtField);
        $("#customerForm p.errorText").eq(1).show();
        $("#errorName").text("*Required Field* Min 5, Max 20, Spaces Allowed");

        disableButton(".btnSaveCustomer");
        disableButton("#btnEditCustomer");
        return false;
    }
}

function validate_CustomerAddress(input, txtField) {

    if (regExCusAddress.test(input)) {
        changeBorderColor("valid", txtField);

        // once the current input field is green change the the border of next input field to red
        if (!validate_CustomerContact(txtContact.val(), txtContact)) {
            changeBorderColor("invalid", txtContact);
            $("#customerForm p.errorText").eq(3).show();
            $("#errorContact").text("*Required Field* Min 10, Max 10, Only Numbers");
        }

        $("#customerForm p.errorText").eq(2).hide();
        return true;

    } else {
        changeBorderColor("invalid", txtField);
        $("#customerForm p.errorText").eq(2).show();
        $("#errorAddress").text("*Required Field* Minimum 5");

        disableButton(".btnSaveCustomer");
        disableButton("#btnEditCustomer");
        return false;
    }
}

function validate_CustomerContact(input, txtField) {
    if (regExCusContact.test(txtContact.val())) {
        changeBorderColor("valid", txtField);
        enableButton(".btnSaveCustomer");
        enableButton("#btnEditCustomer");

        $("#customerForm p.errorText").eq(3).hide();

        // validate_ContactNo(input,txtField);
        // return true;

        if (customerDB.length >= 0) {
            customerDB.forEach(obj => {
                if (obj.getCustomerContact() != input) { // if not a duplicate Contact No
                    return true;

                } else if (obj.getCustomerID() == txtCustomerId.val() && txtContact.val() == obj.getCustomerContact()) { // if its the Contact of the selected Customer
                    return true;

                } else { // if a duplicate Contact No

                    changeBorderColor("invalid", txtField);
                    $("#customerForm p.errorText").eq(3).show();
                    $("#errorContact").text("*Required Field* A Customer with this Contact already exist..");

                    disableButton(".btnSaveCustomer");
                    disableButton("#btnEditCustomer");
                    return false;

                }
            });
        }
        return true;

    } else {
        changeBorderColor("invalid", txtField);
        $("#customerForm p.errorText").eq(3).show();
        $("#errorContact").text("*Required Field* Min 10, Max 10, Only Numbers");

        disableButton(".btnSaveCustomer");
        disableButton("#btnEditCustomer");
        return false;
    }
}

function reset_CustomerForm() {
    txtCustomerId.val("").css('border', '1px solid rgb(206, 212, 218)');
    txtCustomerName.val("").css('border', '1px solid rgb(206, 212, 218)');
    txtAddress.val("").css('border', '1px solid rgb(206, 212, 218)');
    txtContact.val("").css('border', '1px solid rgb(206, 212, 218)');

    $("#customerForm p.errorText").hide();

    txtCustomerId.focus();
    txtCustomerId.attr("disabled", "disabled");

    disableButton(".btnSaveCustomer");
    disableButton("#btnEditCustomer");
    disableButton("#btnDeleteCustomer");

    select_CustomerRow();

    rowSelected = null;
    customerId = null;
}

function validate_CustomerForm() {

    customerId = txtCustomerId.val();
    customerName = txtCustomerName.val();
    customerAddress = txtAddress.val();
    customerContact = txtContact.val();

    validate_CustomerID(customerId, txtCustomerId);
    validate_CustomerName(customerName, txtCustomerName);
    validate_CustomerAddress(customerAddress, txtAddress);
    validate_CustomerContact(customerContact, txtContact);
}

$("#txtCustomerId, #txtCustomerName, #txtAddress, #txtContact").keydown(function (e) {
    $("#btnSearchCustomer").off("click");
    if (e.key === "Tab") {
        e.preventDefault();
    }

    if (e.code === "Enter") {
        e.preventDefault();

    }
});

$("#txtCustomerId").keyup(function (e) {
    input = txtCustomerId.val();
    validate_CustomerID(input, this);

    if (e.code === "Enter" && isBorderGreen(this)) {
        $("#txtCustomerName").focus();
    }
});

$("#txtCustomerName").keyup(function (e) {
    input = txtCustomerName.val();
    validate_CustomerName(input, this);

    if (e.code === "Enter" && isBorderGreen(this)) {
        $("#txtAddress").focus();
    }
});

$("#txtAddress").keyup(function (e) {
    input = txtAddress.val();
    validate_CustomerAddress(input, this);

    if (e.code === "Enter" && isBorderGreen(this)) {
        $("#txtContact").focus();
    }
});

$("#txtContact").keyup(function (e) {
    input = txtContact.val();
    validate_CustomerContact(input, this);

    if (e.code == "Enter" && isBorderGreen(this)) {
        // isCustomerAlreadyExist();
        // checkDB_BeforeSaveCustomer();
        select_CustomerRow();
    }
    $("#tblCustomer-body>tr").off("dblclick");
    delete_CustomerRowOnDblClick();

});

/* -----Clear Fields-------*/

$("#btnClearCustomerFields").click(function () {
    reset_CustomerForm();
    generateNextCustomerID();
    loadAllCustomers();

    txtSearchId.val("");
    select_CustomerRow();
});




