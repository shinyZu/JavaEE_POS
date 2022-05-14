DROP DATABASE IF EXISTS JavaEE_POS;
CREATE DATABASE IF NOT EXISTS JavaEE_POS;
SHOW DATABASES ;
USE JavaEE_POS;
#-------------------
DROP TABLE IF EXISTS Customer;
CREATE TABLE IF NOT EXISTS Customer(
   customerId VARCHAR(15),
   customerName VARCHAR(100),
   customerAddress VARCHAR (100),
   customerContact INT(10),
   CONSTRAINT PRIMARY KEY (customerId)
);
SHOW TABLES ;
DESCRIBE Customer;
#-----------------------
DROP TABLE IF EXISTS Item;
CREATE TABLE IF NOT EXISTS Item(
   itemCode VARCHAR(15),
   description VARCHAR(50),
   unitPrice DOUBLE DEFAULT 0.00,
   qtyOnHand INT DEFAULT 0,
   CONSTRAINT PRIMARY KEY (itemCode)
);
SHOW TABLES ;
DESCRIBE Item;
#------------------------
DROP TABLE IF EXISTS Orders;
CREATE TABLE IF NOT EXISTS Orders(
   orderId VARCHAR(15),
   orderDate DATE,
   orderCost  DOUBLE DEFAULT 0.00,
   discount  DOUBLE DEFAULT 0.00,
   customerId VARCHAR(15),

   CONSTRAINT PRIMARY KEY (orderId),
   CONSTRAINT FOREIGN KEY (customerId) REFERENCES Customer(customerId) ON DELETE CASCADE ON UPDATE CASCADE
);
SHOW TABLES ;
DESCRIBE Orders;
#-----------------------
DROP TABLE IF EXISTS OrderDetails;
CREATE TABLE IF NOT EXISTS OrderDetails(
    orderId VARCHAR(15),
    itemCode VARCHAR(15),
    orderQty INT,

    CONSTRAINT PRIMARY KEY (itemCode, orderId),
    CONSTRAINT FOREIGN KEY (orderId) REFERENCES Orders(orderId) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FOREIGN KEY (itemCode) REFERENCES Item(itemCode) ON DELETE CASCADE ON UPDATE CASCADE
);
SHOW TABLES ;
DESCRIBE OrderDetails;

FROM Student s WHERE s.studentId LIKE '%"+text+"%' OR s.studentName LIKE '%" + text + "%' OR s.address LIKE '%" + text + "%';
SELECT COUNT(customerId) FROM Customer;
SELECT s.studentId FROM Student s ORDER BY s.studentId DESC LIMIT 1;
