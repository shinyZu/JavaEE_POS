let orderId = $("#txtOrderID");
let date = $("#date");

let cmbCustomerId = $("#cmbCustomerId");
let cmbCustomerName = $("#cmbCustomerName");
let txtord_address = $("#address");
let txtord_contact = $("#contact");

let cmbItemCode = $("#cmbItemCode");
let cmbDescription = $("#cmbDescription");
let txtUnitPrice2 = $("#txtUnitPrice2");
let txtQtyOnHand = $("#txtQtyOnHand");
let txtOrderQty = $("#txtOrderQty");

let newOption;
let defaultOption = `<option value="-1" selected disabled hidden >Select</option>`;
let selectedOption;

let orderQty;
let total;
let qtyOnHand;

let selected_cartItem;
let noOfRows = 0;
let cartTotal = 0; // total cost of the cart

var regEx_Discount_Cash = /^[0-9]+$/;

let subTotal;
let balance;
let discount;
let amountPaid;

let allCustomers;
let allItems;
let allOrders;

console.log("Inside Order Controller...");

$(cmbCustomerId).append(defaultOption);
$(cmbCustomerName).append(defaultOption);
$(cmbItemCode).append(defaultOption);
$(cmbDescription).append(defaultOption);

$("#selectItemForm p.errorText").hide();
$("#purchaseForm p.errorText").hide();

(function () {

    disableButton("#btnAddToCart");
    disableButton("#btnDeleteFromCart");
    disableButton("#btnDeleteOrder");
    disableButton("#btnPurchase");

    txtOrderQty.attr("disabled", "disabled");
    $("#txtDiscount").attr("disabled", "disabled");
    $("#txtAmountPaid").attr("disabled", "disabled");

})();

function clearCmbCustomerId() {
    $(cmbCustomerId).empty();
    $(cmbCustomerId).append(defaultOption);
}

function clearCmbCustomerName() {
    $(cmbCustomerName).empty();
    $(cmbCustomerName).append(defaultOption);
}

function clearCmbItemCode() {
    $(cmbItemCode).empty();
    $(cmbItemCode).append(defaultOption);
}

function clearCmbDescription() {
    $(cmbDescription).empty();
    $(cmbDescription).append(defaultOption);
}

/* --------------------------------------------------------*/

function loadCmbCustomerId() {
    console.log("Inside loadCMB ID");
    clearCmbCustomerId();
    // clearCustomerFields();

    let optionValue = -1;
    // for (let customer of customerDB) {
    //     optionValue++;
    //     newOption = `<option value="${optionValue}">${customer.getCustomerID()}</option>`;
    //     $(cmbCustomerId).append(newOption);
    // }

    $.ajax({
        url: "customer?option=GET_ID_NAME",
        method: "GET",
        success: function (resp) {
            for (let c of resp.data) {
                // console.log(resp.data);
                let customer = new Customer(c.id);
                // optionValue++;
                // newOption = `<option value="${optionValue}">${customer.getCustomerID()}</option>`;
                newOption = `<option>${customer.getCustomerID()}</option>`;
                $(cmbCustomerId).append(newOption);
            }
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

function loadCmbCustomerName() {
    console.log("Inside loadCMB Names");
    clearCmbCustomerName();
    // clearCustomerFields();

    let optionValue = -1;
    // for (let customer of customerDB) {
    //     optionValue++;
    //     newOption = `<option value="${optionValue}">${customer.getCustomerName()}</option>`;
    //     $(cmbCustomerName).append(newOption);
    // }

    $.ajax({
        url: "customer?option=GET_ID_NAME",
        method: "GET",
        success: function (resp) {
            for (let c of resp.data) {
                // console.log(resp.data);
                // let customer = new Customer(c.name);
                // optionValue++;
                // newOption = `<option value="${optionValue}">${customer.getCustomerName()}</option>`;
                newOption = `<option>${c.name}</option>`;
                $(cmbCustomerName).append(newOption);
            }
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

function loadCmbItemCode() {
    console.log("Inside loadCMB ItemCode");
    clearCmbItemCode();
    // clearItemFields();

    /*let optionValue = -1;
    for (let item of itemDB) {
        optionValue++;
        newOption = `<option value="${optionValue}">${item.getItemCode()}</option>`;
        // $(txtUnitPrice2).val(item.price);
        $(cmbItemCode).append(newOption);
    }*/

    $.ajax({
        url: "item?option=GET_CODE_DESCRIP",
        method: "GET",
        success: function (resp) {
            for (let item of resp.data) {
                newOption = `<option>${item.itemCode}</option>`;
                $(cmbItemCode).append(newOption);
            }
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

function loadCmbDescription() {
    console.log("Inside loadCMB Description");
    clearCmbDescription();
    // clearItemFields();

    /*let optionValue = -1;
    for (let item of itemDB) {
        optionValue++;
        newOption = `<option value="${optionValue}">${item.getDescription()}</option>`;
        $(cmbDescription).append(newOption);
    }*/

    $.ajax({
        url: "item?option=GET_CODE_DESCRIP",
        method: "GET",
        success: function (resp) {
            for (let item of resp.data) {
                newOption = `<option>${item.description}</option>`;
                $(cmbDescription).append(newOption);
            }
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });

}

/* ---------------------Load Customer Details-------------*/

function loadCustomerDetails(customer) {
    /*cmbCustomerId.val(customerDB.indexOf(custObj));
    cmbCustomerName.val(customerDB.indexOf(custObj));
    txtord_address.val(custObj.getCustomerAddress());
    txtord_contact.val(custObj.getCustomerContact());*/

    cmbCustomerId.val(customer.getCustomerID());
    cmbCustomerName.val(customer.getCustomerName());
    txtord_address.val(customer.getCustomerAddress());
    txtord_contact.val(0 + customer.getCustomerContact());
}

$("#cmbCustomerId").click(function () {
    /*selectedOption = parseInt(cmbCustomerId.val());
    if (selectedOption >= 0) {
        loadCustomerDetails(customerDB[selectedOption]);
    }*/

    selectedOption = cmbCustomerId.val();
    if (selectedOption != null) {
        $.ajax({
            url: "customer?option=SEARCH&customerID=" + selectedOption + "&customerName=",
            method: "GET",
            success: function (resp) {
                response = resp;
                let customer = new Customer(resp.data.id, resp.data.name, resp.data.address, resp.data.contact);
                loadCustomerDetails(customer);
            }
        });
    }
});

$("#cmbCustomerName").click(function () {
    /*selectedOption = parseInt(cmbCustomerName.val());
    if (selectedOption >= 0) {
        loadCustomerDetails(customerDB[selectedOption]);
    }*/

    selectedOption = cmbCustomerName.val();
    if (selectedOption != null) {
        $.ajax({
            url: "customer?option=SEARCH&customerID=&customerName=" + selectedOption,
            method: "GET",
            success: function (resp) {
                response = resp;
                let customer = new Customer(resp.data.id, resp.data.name, resp.data.address, resp.data.contact);
                loadCustomerDetails(customer);
            }
        });
    }
});

/* ---------------------Load Item Details-------------*/

function loadItemDetails(item) {
    /*cmbItemCode.val(itemDB.indexOf(itemObj));
    cmbDescription.val(itemDB.indexOf(itemObj));

    qtyOnHand = parseInt(itemObj.getQtyOnHand());
    response = isItemAlreadyAddedToCart(itemObj.getItemCode());

    if (response) { // if item is already added to cart
        let rowNo = response;
        let orderedQty = $(`#tblInvoice-body>tr:nth-child(${rowNo})`).children(":nth-child(4)").text();
        txtQtyOnHand.val(qtyOnHand - parseInt(orderedQty));

    } else if (response == false) {
        txtQtyOnHand.val(qtyOnHand);
    }

    txtUnitPrice2.val(itemObj.getUnitPrice());*/

    cmbItemCode.val(item.getItemCode());
    cmbDescription.val(item.getDescription());
    txtUnitPrice2.val(item.getUnitPrice());

    qtyOnHand = parseInt(item.getQtyOnHand());
    response = isItemAlreadyAddedToCart(item.getItemCode());

    if (response) { // if item is already added to cart
        let rowNo = response;
        let orderedQty = $(`#tblInvoice-body>tr:nth-child(${rowNo})`).children(":nth-child(4)").text();
        txtQtyOnHand.val(qtyOnHand - parseInt(orderedQty));

    } else if (response == false) {
        txtQtyOnHand.val(qtyOnHand);
    }
}

$("#cmbItemCode").click(function () {
    /* selectedOption = parseInt(cmbItemCode.val());
     if (selectedOption >= 0) {
         loadItemDetails(itemDB[selectedOption]);
         txtOrderQty.removeAttr("disabled");
     }*/

    selectedOption = cmbItemCode.val();
    if (selectedOption != null) {
        $.ajax({
            url: "item?option=SEARCH&itemCode=" + selectedOption + "&description=",
            method: "GET",
            success: function (resp) {
                response = resp;
                let item = new Item(resp.data.itemCode, resp.data.description, resp.data.unitPrice, resp.data.qtyOnHand);
                loadItemDetails(item);
                txtOrderQty.removeAttr("disabled");
            }
        });
    }
});

$("#cmbDescription").click(function () {
    /*selectedOption = parseInt(cmbDescription.val());
    if (selectedOption >= 0) {
        loadItemDetails(itemDB[selectedOption]);
        txtOrderQty.removeAttr("disabled");
    }*/

    selectedOption = cmbDescription.val();
    if (selectedOption != null) {
        $.ajax({
            url: "item?option=SEARCH&itemCode=&description=" + selectedOption,
            method: "GET",
            success: function (resp) {
                response = resp;
                let item = new Item(resp.data.itemCode, resp.data.description, resp.data.unitPrice, resp.data.qtyOnHand);
                loadItemDetails(item);
                txtOrderQty.removeAttr("disabled");
            }
        });
    }
});

/* ---------------------Clear Fields & Invoice Table-------------*/

function clearItemFields() {
    console.log("inside clear item fields...")
    loadCmbItemCode();
    loadCmbDescription();
    txtUnitPrice2.val("");
    txtQtyOnHand.val("");
    txtOrderQty.val("").css('border', '1px solid rgb(206, 212, 218)');
    $("#selectItemForm p.errorText").hide();

    disableButton("#btnAddToCart");
}

function clearCustomerFields() {
    console.log("inside clear customer fields...")
    loadCmbCustomerId();
    loadCmbCustomerName();
    txtord_address.val("");
    txtord_contact.val("");
}

function clearInvoiceFields() {
    $("#txtTotal, #txtDiscount, #txtSubTotal, #txtAmountPaid, #txtBalance").val("");

    changeBorderColor("default", $("#txtDiscount"));
    $("#purchaseForm input#txtDiscount+p.errorText").hide();

    changeBorderColor("default", $("#txtAmountPaid"));
    $("#purchaseForm input#txtAmountPaid+p.errorText").hide();

    changeBorderColor("default", $("#txtBalance"));

    disableButton("#btnPurchase");
}

$("#btnClearSelectItemFields").click(function (e) {
    clearItemFields();
});

function clearInvoiceTable() {
    $("#tblInvoice-body").empty();
    noOfRows = 0;
    rowSelected = null;
}

$("#btnClearAllFields").click(function (e) {
    clearCustomerFields();
    clearItemFields();
    clearInvoiceFields();
    clearInvoiceTable();

    generateNextOrderID();
    disableButton("#btnDeleteOrder");
    $("#txtDiscount").attr("disabled", "disabled");
    $("#txtAmountPaid").attr("disabled", "disabled");
    enableCmbBoxes();

    select_OrderDetailRow();
});

function disableCmbBoxes() {
    cmbCustomerId.attr("disabled", "disabled");
    cmbCustomerName.attr("disabled", "disabled");
    cmbItemCode.attr("disabled", "disabled");
    cmbDescription.attr("disabled", "disabled");
    $("#txtOrderQty").attr("disabled", "disabled");
    $("#txtDiscount").attr("disabled", "disabled");
    $("#txtAmountPaid").attr("disabled", "disabled");
}

function enableCmbBoxes() {
    cmbCustomerId.removeAttr("disabled");
    cmbCustomerName.removeAttr("disabled");
    cmbItemCode.removeAttr("disabled");
    cmbDescription.removeAttr("disabled");
    $("#txtOrderQty").removeAttr("disabled");
}

/* --------------------Select from Cart------------- */

function getAllItems() {
    $.ajax({
        url: "item?option=GETALL",
        method: "GET",
        async: false,
        success: function (resp) {
            allItems = resp.data;
        },
        error: function (ob, textStatus, error) {
            alert(textStatus);
            console.log(ob);
        }
    });
}

function select_CartRow() {
    getAllItems();

    $("#tblInvoice-body>tr").click(function (e) {
        enableButton("#btnDeleteFromCart");

        rowSelected = this;
        itemCode = $(this).children(':first-child').text();
        orderQty = $(this).children(':nth-child(4)').text();

        /*itemDB.forEach(obj => {
            if (itemCode == obj.getItemCode()) {
                loadItemDetails(obj);
                txtOrderQty.val(orderQty);
            }
        });*/

        for (let i of allItems) {
            let item = new Item(i.itemCode, i.description, i.unitPrice, i.qtyOnHand);

            if (itemCode === item.getItemCode()) {
                loadItemDetails(item);
                txtOrderQty.val(orderQty);
            }
        }

        $("#btnDeleteFromCart").off("click");
        $("#btnDeleteFromCart").click(function (e) {
            if (rowSelected != null) {
                itemCode = $(rowSelected).children(':first-child').text();

                Swal.fire({
                    text: `Do you really need to Remove Item ${itemCode} from Cart..?`,
                    icon: 'question',
                    showCancelButton: true,
                    confirmButtonText: 'Remove',
                    confirmButtonColor: '#e66767',
                    customClass: {
                        cancelButton: 'order-1 right-gap',
                        confirmButton: 'order-2',
                    },
                    allowOutsideClick: false,

                }).then(result => {
                    if (result.isConfirmed) {
                        $(rowSelected).remove();
                        clearItemFields();
                        clearInvoiceFields();
                        rowSelected = null;

                        disableButton("#btnDeleteFromCart");
                        noOfRows--;
                        calculate_OrderCost();
                        reset_InvoiceOnCartUpdate();
                    }
                });

            } else {
                alertText = "Please select a row to delete...";
                display_Alert("", alertText, "warning");
            }

            // disableButton("#btnDeleteFromCart");
            // noOfRows--;
            // calculate_OrderCost();
            // reset_InvoiceOnCartUpdate();
        });

        validate_OrderQty(parseInt(txtOrderQty.val()), txtOrderQty);

    });
}

/* ------------------------Add To Cart------------ */

function validate_OrderQty(input, txtField) {
    // orderQty =  parseInt(txtOrderQty.val());
    orderQty = txtOrderQty.val();
    // orderQty =  parseInt(input);
    // orderQty =  input;
    qtyOnHand = parseInt(txtQtyOnHand.val());

    if (regExQty.test(input)) {
        // if (regEx_Discount_Cash.test(input)) {

        if (input < qtyOnHand) {
            changeBorderColor("valid", txtField);
            $("#selectItemForm p.errorText").hide();
            enableButton("#btnAddToCart");

        } else if (input > qtyOnHand) {
            changeBorderColor("invalid", txtField);
            $("#selectItemForm p.errorText").show();
            $("small#errorQty").text("Please enter an amount lower than " + qtyOnHand);
            disableButton("#btnAddToCart");

        }

    } else {
        changeBorderColor("invalid", txtField);
        $("#selectItemForm p.errorText").show();
        // $("small#errorQty").text("Please enter an amount lower than "+qtyOnHand);
        $("small#errorQty").text("Please enter only numbers");
        disableButton("#btnAddToCart");
    }
}

function isItemAlreadyAddedToCart(code) {
    let codeInCart;
    let rowNo = 1;
    do {
        codeInCart = $(`#tblInvoice-body>tr:nth-child(${rowNo})`).children(":nth-child(1)").text();
        if (code == codeInCart) {
            return rowNo; // if item is already added to cart
        }
        rowNo++;
    } while (codeInCart != "");
    return false; // if item is not yet added to the cart
}

function addToCart() {

    /*itemCode = itemDB[parseInt(cmbItemCode.val())].getItemCode();
    description = itemDB[parseInt(cmbItemCode.val())].getDescription();
    unitPrice = parseFloat(txtUnitPrice2.val());
    orderQty = parseInt(txtOrderQty.val());
    total = parseFloat(unitPrice * orderQty);
    qtyOnHand = txtQtyOnHand.val();*/

    itemCode = cmbItemCode.val();
    description = cmbDescription.val();
    unitPrice = parseFloat(txtUnitPrice2.val());
    orderQty = parseInt(txtOrderQty.val());
    total = parseFloat(unitPrice * orderQty);
    qtyOnHand = txtQtyOnHand.val();

    response = isItemAlreadyAddedToCart(itemCode);

    if (response) { // if item is already added to cart

        let rowToUpdate = $(`#tblInvoice-body>tr:nth-child(${response})`);
        let prevQty = parseInt(rowToUpdate.children(":nth-child(4)").text());
        rowToUpdate.children(":nth-child(4)").text(prevQty + orderQty);
        rowToUpdate.children(":nth-child(5)").text(parseFloat((prevQty + orderQty) * unitPrice).toFixed(2));

    } else if (response == false) { // if item is not yet added to the cart

        newRow = `<tr>
                    <td>${itemCode}</td>
                    <td>${description}</td>
                    <td>${txtUnitPrice2.val()}</td>
                    <td>${txtOrderQty.val()}</td>
                    <td>${parseFloat(total).toFixed(2)}</td>
                 </tr>`;

        $("#tblInvoice-body").append(newRow);
        noOfRows++;
    }
    clearItemFields();
    disableButton("#btnDeleteFromCart");

    $("#txtDiscount").removeAttr("disabled");
    $("#txtAmountPaid").removeAttr("disabled");

    calculate_OrderCost();
    calculate_subTotal($("#txtDiscount").val());

    reset_InvoiceOnCartUpdate();

}

$("#txtOrderQty").keyup(function (e) {
    // validate_OrderQty(parseInt(txtOrderQty.val()),txtOrderQty);
    validate_OrderQty(txtOrderQty.val(), txtOrderQty);

    if (e.code === "Enter" && isBorderGreen(this)) {
        addToCart();
        rowSelected = null;
    }
    select_CartRow();

    $("#tblInvoice-body>tr").off("dblclick");
    delete_cartRowOnDblClick();
});

$("#btnAddToCart").click(function (e) {
    if (isBorderGreen(txtOrderQty)) {
        addToCart();
        rowSelected = null;
    }
    select_CartRow();
    delete_cartRowOnDblClick();
});

/* ------------------------Delete from Cart------------ */

function delete_cartRowOnDblClick() {
    $("#tblInvoice-body>tr").off("dblclick");
    $("#tblInvoice-body>tr").dblclick(function () {
        itemCode = $(rowSelected).children(':first-child').text();

        Swal.fire({
            text: `Do you really need to Remove Item ${itemCode} from Cart..?`,
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Remove',
            confirmButtonColor: '#e66767',
            customClass: {
                cancelButton: 'order-1 right-gap',
                confirmButton: 'order-2',
            },
            allowOutsideClick: false,

        }).then(result => {
            if (result.isConfirmed) {
                $(rowSelected).remove();
                clearItemFields();
                clearInvoiceFields();
                rowSelected = null;

                disableButton("#btnDeleteFromCart");
                noOfRows--;
                calculate_OrderCost();
                reset_InvoiceOnCartUpdate();
            }
        });

        // disableButton("#btnDeleteFromCart");
        // noOfRows--;
        // calculate_OrderCost();
        // reset_InvoiceOnCartUpdate();
    });
}

/* ---------------Calculate Order Total, Subtotal, Balance------------------ */

function reset_InvoiceOnCartUpdate() {
    if (noOfRows == 0) {
        $("#txtDiscount").val("");
    }

    $("#txtAmountPaid, #txtBalance").val("");
    changeBorderColor("default", $("#txtBalance"));
    changeBorderColor("default", $("#txtAmountPaid"));
}

function calculate_OrderCost() {
    cartTotal = 0.00;
    let colTotal = 0; // column "Total" in Table
    let rowNo = 1;

    if (noOfRows == 0) {
        $("#txtTotal").val("0.00");
        cartTotal = 0;

    } else {
        do {
            colTotal = parseFloat($(`#tblInvoice-body>tr:nth-child(${rowNo})`).children(":nth-child(5)").text());

            // cartTotal += parseInt(colTotal).toFixed(2);
            cartTotal += parseFloat(colTotal);
            $("#txtTotal").val(parseFloat(cartTotal).toFixed(2));
            rowNo++;

        } while (rowNo <= noOfRows);
    }
    calculate_subTotal($("#txtDiscount").val());
}

function calculate_subTotal(discount) {

    if ($("#txtDiscount").val() == '') {
        subTotal = cartTotal;
        $("#txtSubTotal").val(parseFloat(subTotal).toFixed(2));
        return;
    }

    subTotal = cartTotal * (100 - discount) / 100;
    $("#txtSubTotal").val(parseFloat(subTotal).toFixed(2));
}

function validate_Discount_Cash(input, txtField, txtFieldId) {  // validate discount & cash fields

    if (regEx_Discount_Cash.test(input)) {
        changeBorderColor("valid", txtField);

        // $("#purchaseForm input#txtDiscount+p.errorText").hide();
        $(`#purchaseForm input${txtFieldId}+p.errorText`).hide();

        // if (txtFieldId == "txtAmountPaid") {
        //     calculate_Balance(input);
        // }

        return true;

    } else {
        changeBorderColor("invalid", txtField);
        $(`#purchaseForm input${txtFieldId}+p.errorText`).show();
        $(`#purchaseForm input${txtFieldId}+p.errorText small`).text(" Enter Only Numbers");

        return false;
    }
}

function calculate_Balance(amountPaid) {
    balance = parseFloat(amountPaid - subTotal).toFixed(2);

    $("#txtBalance").val(balance);

    if (balance < 0) {
        changeBorderColor("invalid", $("#txtAmountPaid"));
        changeBorderColor("invalid", $("#txtBalance"));
        $("#purchaseForm input#txtAmountPaid+p.errorText").show();
        $("small#errorPaid").text("Insufficient Credit");

    } else {
        changeBorderColor("valid", $("#txtAmountPaid"));
        changeBorderColor("default", $("#txtBalance"));
        $("#purchaseForm input#txtAmountPaid+p.errorText").hide();

        enableButton("#btnPurchase");
    }
}

$("#txtDiscount, #txtAmountPaid").keydown(function (e) {
    if (e.key === "Tab") {
        e.preventDefault();
    }
});

$("#txtDiscount").keyup(function (e) {
    // discount = parseInt($("#txtDiscount").val());
    discount = $("#txtDiscount").val();
    // let isValid = validate_Discount_Cash(discount,$("#txtDiscount"),"#txtDiscount");
    validate_Discount_Cash(discount, $("#txtDiscount"), "#txtDiscount");

    calculate_subTotal(discount);

    if (e.code === "Enter" && isBorderGreen(this)) {
        $("#txtAmountPaid").focus();
    }
});

$("#txtAmountPaid").keyup(function (e) {
    // amountPaid = parseInt($("#txtAmountPaid").val());
    amountPaid = $("#txtAmountPaid").val();
    isValid = validate_Discount_Cash(amountPaid, $("#txtAmountPaid"), "#txtAmountPaid");
    // validate_Discount_Cash(amountPaid,$("#txtAmountPaid"),"#txtAmountPaid");

    if (isValid) {
        calculate_Balance(amountPaid);

        if (e.code === "Enter" && isBorderGreen(this)) {
            $("#btnPurchase").focus();
        }
    }
});

function transaction() {

    let rowNo = 1;
    let orderDetail;

    $.ajax({
        url: "orders",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify(orderObj),
        async: false,
        success: function (resp1) {
            console.log(4);
            response = resp1;
            if (resp1.status === 200) {
                console.log(5);
                // toastr.success(resp.message);
                if (resp1.message === "O_ACCEPTED") {
                    console.log("order ACCEPTED");

                    if (noOfRows === 0) {
                        alert("Empty Table..");

                    } else {
                        getAllItems();
                        do {
                            itemCode = $(`#tblInvoice-body>tr:nth-child(${rowNo})`).children(":nth-child(1)").text();
                            orderQty = $(`#tblInvoice-body>tr:nth-child(${rowNo})`).children(":nth-child(4)").text();

                            orderDetail = new OrderDetails(orderId, itemCode, orderQty);

                            $.ajax({
                                url: "orderDetails",
                                method: "POST",
                                contentType: "application/json",
                                data: JSON.stringify(
                                    {
                                        orderId: orderDetail.getOrderId(),
                                        itemCode: orderDetail.getItemCode(),
                                        orderQty: orderDetail.getOrderQty()
                                    }
                                ),
                                success: function (resp2) {
                                    response = resp2;
                                    if (resp2.status === 200) {
                                        // toastr.success(resp2.message);
                                        if (resp2.message === "D_ACCEPTED") {
                                            //
                                        }
                                    } else {
                                        toastr.error(resp2.message);
                                    }
                                },
                                error: function (ob, textStatus, error) {
                                    console.log(ob);
                                }
                            });

                            for (let i of allItems) {
                                let item = new Item(i.itemCode, i.description, i.unitPrice, i.qtyOnHand);
                                if (item.getItemCode() === itemCode) {
                                    qtyOnHand = item.getQtyOnHand();
                                }
                            }

                            let index = 0;

                            for (let i in allItems) {
                                // let item = new Item(i.itemCode, i.description, i.unitPrice, i.qtyOnHand);
                                if (allItems[i].id === itemCode) {
                                    qtyOnHand = allItems[i].qtyOnHand;
                                    index = i;
                                }
                            }

                            rowNo++;

                        } while (rowNo <= noOfRows);
                    }
                }
            } else {
                console.log(6);
                toastr.error(resp.message);
            }

        },
        error: function (ob, textStatus, error) {
            console.log(7);
            console.log(ob);
            alert(textStatus);
        }
    });
}

/* --------------------Place Order------------------------ */

function place_Order(orderId) {
    console.log(1);
    console.log(orderId);

    customerId = cmbCustomerId.val();
    let newOrder = new Orders(orderId, date.val(), subTotal, discount, customerId);
    console.log(2);

    /*if (ordersDB.length == 0) {
        ordersDB.push(newOrder);

    } else {
        for (let obj of ordersDB) {
            if (orderId == obj.getOrderId()) {
                alertText = "Duplicate Order ID " + orderId + "\nPlease start a New Order";
                display_Alert("", alertText, "warning");

                return;
            }
        }
        ordersDB.push(newOrder);
    }*/

    let orderObj = {
        orderId: newOrder.getOrderId(),
        date: newOrder.getOrderDate(),
        // subTotal: parseFloat(newOrder.getOrderCost()).toFixed(2),
        // subTotal: subTotal.toFixed(2),
        subTotal: newOrder.getOrderCost().toFixed(2),
        discount: newOrder.getOrderDiscount(),
        customerId: newOrder.getCustomerID()
        // setAutoCommit: "FALSE"
    }

    console.log(3);
    console.log(orderObj);

    // transaction();

    $.ajax({
        url: "orders",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify(orderObj),
        async: false,
        success: function (resp1) {
            console.log(4);
            response = resp1;
            if (resp1.status === 200) {
                console.log(5);
                toastr.success(resp1.message);

            } else {
                console.log(6);
                toastr.error(resp1.message);
            }
        },
        error: function (ob, textStatus, error) {
            console.log(7);
            console.log(ob);
            alert(textStatus);
        }
    });

    // $("#totalOrders").text("0" + ordersDB.length);

    let rowNo = 1;
    let orderDetail;

    if (noOfRows === 0) {
        alert("Empty Table..");

    } else {
        getAllItems();
        do {
            itemCode = $(`#tblInvoice-body>tr:nth-child(${rowNo})`).children(":nth-child(1)").text();
            orderQty = $(`#tblInvoice-body>tr:nth-child(${rowNo})`).children(":nth-child(4)").text();

            orderDetail = new OrderDetails(orderId, itemCode, orderQty);
            // orderDetailDB.push(orderDetail);
            console.log(orderId);

            $.ajax({
                url: "orderDetails",
                method: "POST",
                contentType: "application/json",
                data: JSON.stringify(
                    {
                        orderId: orderDetail.getOrderId(),
                        itemCode: orderDetail.getItemCode(),
                        orderQty: orderDetail.getOrderQty(),
                    }
                ),
                success: function (resp) {
                    response = resp;
                    if (resp.status == 200) {
                        toastr.success(resp.message);
                    } else {
                        toastr.error(resp.message);
                    }
                },
                error: function (ob, textStatus, error) {
                    console.log(ob);
                }
            });

            // for (let obj of orderDetailDB) {
            //     if (orderId != obj.getOrderId()) {
            //         orderDetailDB.push(orderDetail);

            //     } else {
            //         alert("You cannot update an existing Order...");
            //     }
            // }

            /*itemDB.forEach(obj => {
                if (obj.getItemCode() == itemCode) {
                    qtyOnHand = obj.getQtyOnHand();
                }
            });*/

            console.log(allItems);

            /*allItems.forEach(obj => {
                if (obj.getItemCode() === itemCode) {
                    qtyOnHand = obj.getQtyOnHand();
                }
            });*/

            for (let i of allItems) {
                let item = new Item(i.itemCode, i.description, i.unitPrice, i.qtyOnHand);
                if (item.getItemCode() === itemCode) {
                    qtyOnHand = item.getQtyOnHand();
                }
            }

            let index = 0;

            /*for (let i in itemDB) {
                if (itemDB[i].getItemCode() == itemCode) {
                    qtyOnHand = itemDB[i].getQtyOnHand();
                    index = i;
                }
            }*/

            for (let i in allItems) {
                // let item = new Item(i.itemCode, i.description, i.unitPrice, i.qtyOnHand);
                if (allItems[i].id === itemCode) {
                    qtyOnHand = allItems[i].qtyOnHand;
                    index = i;
                }
            }

            /*let newQtyOnHand = qtyOnHand - parseInt(orderQty);
            allItems[index].setQtyOnHand(newQtyOnHand);
            // loadAllItems(itemDB);
            loadAllItems();*/
            rowNo++;

        } while (rowNo <= noOfRows);
    }
    console.log(8);
    // select_OrderDetailRow();
    console.log(9);
    // select_ItemRow();
    console.log(10);
}

function reset_Forms() {
    date.val("");

    clearCustomerFields();
    clearItemFields();
    clearInvoiceFields();
}

function reset_Table() {
    $("#tblInvoice-body>tr").remove();
    noOfRows = 0;
}

function getAllCustomers() {
    $.ajax({
        url: "customer?option=GETALL",
        method: "GET",
        async: false,
        success: function (resp) {
            console.log(resp.data);
            allCustomers = resp.data;
        },
        error: function (ob, textStatus, error) {
            alert(textStatus);
            console.log(ob);
        }
    });
}

function getAllOrders() {
    $.ajax({
        url: "orders?option=GETALL",
        method: "GET",
        success: function (res) {
            console.log(res);
            allOrders = res.data;
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

function load_TblCustomerOrder() {
    /*$("#tblOrders-body").empty();

    for (let ord_obj of ordersDB) {

        for (let cust_obj of customerDB) {

            if (ord_obj.getCustomerID() == cust_obj.getCustomerID()) {

                newRow = `<tr>
                            <td>${ord_obj.getOrderId()}</td>
                            <td>${ord_obj.getCustomerID()}</td>
                            <td>${cust_obj.getCustomerName()}</td>
                            <td>${cust_obj.getCustomerContact()}</td>
                            <td>${parseFloat(ord_obj.getOrderCost()).toFixed(2)}</td>
                            <td>${ord_obj.getOrderDate()}</td>
                        </tr>`;

            }
        }
        $("#tblOrders-body").append(newRow);
    }*/

    console.log("inside load All Orders");
    $("#tblOrders-body").empty();

    getAllCustomers();
    /*getAllOrders();

    for (let o of allOrders) {
        let order = new Orders(o.orderId, o.orderDate, o.orderCost, o.discount, o.customerId);
        console.log(allCustomers);

        for (let c of allCustomers) {
            let customer = new Customer(c.id, c.name, c.address, c.contact);

            if (order.getCustomerID() === customer.getCustomerID()) {

                newRow = `<tr>
                            <td>${order.getOrderId()}</td>
                            <td>${order.getCustomerID()}</td>
                            <td>${customer.getCustomerName()}</td>
                            <td>0${customer.getCustomerContact()}</td>
                            <td>${parseFloat(order.getOrderCost()).toFixed(2)}</td>
                            <td>${order.getOrderDate()}</td>
                        </tr>`;

            }
        }
        $("#tblOrders-body").append(newRow);
    }*/

    $.ajax({
        url: "orders?option=GETALL",
        method: "GET",
        success: function (res) {
            console.log(res);

            for (let o of res.data) {
                let order = new Orders(o.orderId, o.orderDate, o.orderCost, o.discount, o.customerId);
                console.log(allCustomers);

                for (let c of allCustomers) {
                    let customer = new Customer(c.id, c.name, c.address, c.contact);

                    if (order.getCustomerID() === customer.getCustomerID()) {

                        newRow = `<tr>
                             <td>${order.getOrderId()}</td>
                             <td>${order.getCustomerID()}</td>
                             <td>${customer.getCustomerName()}</td>
                             <td>0${customer.getCustomerContact()}</td>
                             <td>${parseFloat(order.getOrderCost()).toFixed(2)}</td>
                             <td>${order.getOrderDate()}</td>
                         </tr>`;

                    }
                }
                $("#tblOrders-body").append(newRow);
            }
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
        }
    });
}

$("#btnPurchase").click(function (e) {
    if (cmbCustomerId.val() == null) {
        alertText = "Please select a Customer....";
        display_Alert("", alertText, "warning");

    } else if (date.val() == "") {
        alertText = "Please select a Date....";
        display_Alert("", alertText, "warning");

    } else {
        Swal.fire({
            text: "Are you sure you want to Place this Order..?",
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Purchase',
            confirmButtonColor: '#ff7f50',
            customClass: {
                cancelButton: 'order-1 right-gap',
                confirmButton: 'order-2',
            },
            allowOutsideClick: false,
            returnFocus: false,

        }).then(result => {
            if (result.isConfirmed) {
                place_Order(orderId.val());
                // toastr.success("Order Placed Successfully...");
                console.log(11);
                load_TblCustomerOrder();
                generateNextOrderID();

                reset_Forms();
                reset_Table();

                select_OrderDetailRow();
            }
        });
    }
});

/* ------------------Search Order------------------------- */

// find() --> looks at the children of the current selection for a match
// filter() --> looks at the current selection for a match
// each()-->  used to iterate over any collection, whether it is an object or an array. 

/* --------------------------------------------------------------*/

$("#txtSearchOrder").keyup(function (e) {
    searchValue = $(this).val();

    $("#tblOrders-body>tr").each(function () {
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

/* ------------Load Order Details when OrderID is selected-----------*/

function select_OrderDetailRow() {

    $("#tblOrders-body>tr").off("click");
    $("#tblOrders-body>tr").click(function (e) {

        clearInvoiceTable();
        disableCmbBoxes();

        rowSelected = this;
        let orderID = $(this).children(":nth-child(1)").text();

        let order_obj;
        let cust_obj;
        let item_obj;

        let orderDetail_arr = [];

        for (let obj of ordersDB) {
            if (obj.getOrderId() == orderID) {
                order_obj = obj;
            }
        }

        for (let obj of customerDB) {
            if (order_obj.getCustomerID() == obj.getCustomerID()) {
                cust_obj = obj;
            }
        }

        let index = 0;
        for (let i in orderDetailDB) {
            if (orderID == orderDetailDB[i].getOrderId()) {
                orderDetail_arr[index++] = orderDetailDB[i];
            }
        }

        orderId.val(orderID);
        date.val(order_obj.getOrderDate());

        cmbCustomerId.val(customerDB.indexOf(cust_obj));
        cmbCustomerName.val(customerDB.indexOf(cust_obj));
        txtord_address.val(cust_obj.getCustomerAddress())
        txtord_contact.val(cust_obj.getCustomerContact());

        for (let i = 0; i < orderDetail_arr.length; i++) {

            for (let obj of itemDB) {
                if (orderDetail_arr[i].getItemCode() == obj.getItemCode()) {
                    item_obj = obj;
                }
            }

            let unitPrice = item_obj.getUnitPrice();
            orderQty = orderDetail_arr[i].getOrderQty();
            total = unitPrice * orderQty;

            newRow = `<tr>
                        <td>${item_obj.getItemCode()}</td>
                        <td>${item_obj.getDescription()}</td>
                        <td>${unitPrice}</td>
                        <td>${orderQty}</td>
                        <td>${parseFloat(total).toFixed(2)}</td>
                    </tr>`;

            $("#tblInvoice-body").append(newRow);
            noOfRows++;
        }

        calculate_OrderCost();
        discount = order_obj.getOrderDiscount();
        $("#txtDiscount").val(discount)
        calculate_subTotal(discount);

        enableButton("#btnDeleteOrder");

    });
}

/* -------------------------------Delete Order------------------------*/

$("#btnDeleteOrder").click(function (e) {
    let orderID = orderId.val();

    Swal.fire({
        text: "Are you sure you want to Delete this Order..?",
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Delete',
        confirmButtonColor: '#e66767',
        customClass: {
            cancelButton: 'order-1 right-gap',
            confirmButton: 'order-2',
        },
        allowOutsideClick: false,

    }).then(result => {
        if (result.isConfirmed) {

            for (let i in ordersDB) {
                if (orderID == ordersDB[i].getOrderId()) {
                    ordersDB.splice(i, 1);
                    break;
                }
            }

            $("#totalOrders").text("0" + ordersDB.length);

            for (let i in orderDetailDB) {
                if (orderID == orderDetailDB[i].getOrderId()) {
                    orderDetailDB.splice(i, 1);
                    i--;
                }
            }

            clearCustomerFields();
            clearInvoiceFields();
            clearInvoiceTable();

            generateNextOrderID();
            disableButton("#btnDeleteOrder");
            enableCmbBoxes();
            load_TblCustomerOrder();

        }
    });

    select_OrderDetailRow();
});