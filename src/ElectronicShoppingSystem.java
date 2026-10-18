import java.util.*;

// ==========================================
// 1. PRODUCT, CATEGORY, SELLER, SUPPLIER
// ==========================================

class Category {
    private int categoryId;
    private String name;
    private String description;

    public Category(int categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }

    public int getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public void updateCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

class Seller {
    private int sellerId;
    private String name;
    private String contactInfo;

    public Seller(int sellerId, String name, String contactInfo) {
        this.sellerId = sellerId;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public int getSellerId() { return sellerId; }
    public String getName() { return name; }

    public void addProduct(Product product) {
        System.out.println("Seller " + name + " listed product: " + product.getName());
    }

    public void viewSales() {
        System.out.println("Displaying sales report for seller: " + name);
    }
}

class Supplier {
    private int supplierId;
    private String name;
    private String contactInfo;
    private String address;

    public Supplier(int supplierId, String name, String contactInfo, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.address = address;
    }

    public int getSupplierId() { return supplierId; }
    public String getName() { return name; }
}

class Product {
    private int productId;
    private String name;
    private double price;
    private String description;
    private Category category;
    private Seller seller;
    private Supplier supplier;

    public Product(int productId, String name, double price, String description,
                   Category category, Seller seller, Supplier supplier) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.seller = seller;
        this.supplier = supplier;
    }

    public int getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    public void updatePrice(double newPrice) {
        this.price = newPrice;
    }

    public boolean checkAvailability() {
        return true; 
    }

    public String getDetails() {
        return String.format("[%d] %s - $%.2f (%s)", productId, name, price, description);
    }
}

// ==========================================
// 2. CART & CUSTOMER
// ==========================================

class Cart {
    private int cartId;
    private Date createdAt;
    private String status;
    private List<Product> items;

    public Cart(int cartId) {
        this.cartId = cartId;
        this.createdAt = new Date();
        this.status = "ACTIVE";
        this.items = new ArrayList<>();
    }

    public void addProduct(Product product) {
        items.add(product);
        System.out.println(product.getName() + " added to cart.");
    }

    public void removeProduct(Product product) {
        items.remove(product);
        System.out.println(product.getName() + " removed from cart.");
    }

    public List<Product> viewCart() {
        return Collections.unmodifiableList(items);
    }

    public void clearCart() {
        items.clear();
    }

    public double calculateTotal() {
        return items.stream().mapToDouble(Product::getPrice).sum();
    }
}

class Customer {
    private int customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Cart cart;

    public Customer(int customerId, String name, String email, String phone, String address) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.cart = new Cart(customerId);
    }

    public int getCustomerId() { return customerId; }
    public String getName() { return name; }
    public Cart getCart() { return cart; }

    public void addToCart(Product product) {
        cart.addProduct(product);
    }

    public Purchase makePurchase(int purchaseId, String paymentMethod) {
        if (cart.viewCart().isEmpty()) {
            System.out.println("Cart is empty. Cannot make purchase.");
            return null;
        }

        double total = cart.calculateTotal();
        Purchase purchase = new Purchase(purchaseId, new Date(), total, this);

        Payment payment = new Payment(purchaseId * 10, total, paymentMethod, new Date(), purchase);
        boolean paid = payment.processPayment();

        if (paid) {
            purchase.setPayment(payment);
            purchase.placeOrder();
            cart.clearCart();
            return purchase;
        } else {
            System.out.println("Payment failed. Order not placed.");
            return null;
        }
    }
}

// ==========================================
// 3. PURCHASE, PAYMENT, AFTER-SALES SERVICE
// ==========================================

class Payment {
    private int paymentId;
    private double amount;
    private String paymentMethod;
    private Date paymentDate;
    private String status;
    private Purchase purchase;

    public Payment(int paymentId, double amount, String paymentMethod, Date paymentDate, Purchase purchase) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.status = "PENDING";
        this.purchase = purchase;
    }

    public boolean processPayment() {
        this.status = "SUCCESS";
        System.out.println("Payment of $" + amount + " via " + paymentMethod + " processed successfully.");
        return true;
    }

    public void refundPayment() {
        this.status = "REFUNDED";
        System.out.println("Payment refunded.");
    }
}

class Purchase {
    private int purchaseId;
    private Date purchaseDate;
    private double totalAmount;
    private String status;
    private Customer customer;
    private Payment payment;
    private List<AfterSalesService> serviceRequests;

    public Purchase(int purchaseId, Date purchaseDate, double totalAmount, Customer customer) {
        this.purchaseId = purchaseId;
        this.purchaseDate = purchaseDate;
        this.totalAmount = totalAmount;
        this.status = "CREATED";
        this.customer = customer;
        this.serviceRequests = new ArrayList<>();
    }

    public void setPayment(Payment payment) { this.payment = payment; }

    public void placeOrder() {
        this.status = "CONFIRMED";
        System.out.println("Order #" + purchaseId + " confirmed for " + customer.getName());
    }

    public void cancelOrder() {
        this.status = "CANCELLED";
        if (payment != null) payment.refundPayment();
    }

    public void attachServiceRequest(AfterSalesService service) {
        serviceRequests.add(service);
    }

    public int getPurchaseId() { return purchaseId; }
}

class AfterSalesService {
    private int serviceId;
    private String serviceType;
    private Date requestDate;
    private String status;
    private String resolution;
    private Purchase purchase;

    public AfterSalesService(int serviceId, String serviceType, Purchase purchase) {
        this.serviceId = serviceId;
        this.serviceType = serviceType;
        this.requestDate = new Date();
        this.status = "OPEN";
        this.resolution = "Pending inspection";
        this.purchase = purchase;
        this.purchase.attachServiceRequest(this);
    }

    public void raiseRequest() {
        System.out.println("Service request #" + serviceId + " (" + serviceType + ") opened for order #" + purchase.getPurchaseId());
    }

    public void closeRequest(String finalResolution) {
        this.status = "CLOSED";
        this.resolution = finalResolution;
        System.out.println("Service request #" + serviceId + " closed: " + finalResolution);
    }
}

// ==========================================
// 4. OUTLET, STAFF, INVENTORY, STOCK
// ==========================================

class Staff {
    private int staffId;
    private String name;
    private String role;
    private String contactInfo;

    public Staff(int staffId, String name, String role, String contactInfo) {
        this.staffId = staffId;
        this.name = name;
        this.role = role;
        this.contactInfo = contactInfo;
    }

    public void assignRole(String newRole) { this.role = newRole; }
}

class Stock {
    private int stockId;
    private int quantity;
    private Date lastUpdated;

    public Stock(int stockId, int initialQuantity) {
        this.stockId = stockId;
        this.quantity = initialQuantity;
        this.lastUpdated = new Date();
    }

    public void increaseStock(int amount) {
        this.quantity += amount;
        this.lastUpdated = new Date();
    }

    public void decreaseStock(int amount) {
        if (amount <= this.quantity) {
            this.quantity -= amount;
            this.lastUpdated = new Date();
        } else {
            System.out.println("Insufficient stock.");
        }
    }

    public int getQuantity() { return quantity; }
}

class Inventory {
    private int inventoryId;
    private Product product;
    private Stock stock;

    public Inventory(int inventoryId, Product product, Stock stock) {
        this.inventoryId = inventoryId;
        this.product = product;
        this.stock = stock;
    }

    public void updateQuantity(int delta) {
        if (delta > 0) stock.increaseStock(delta);
        else stock.decreaseStock(Math.abs(delta));
    }

    public String getStockDetails() {
        return "Product: " + product.getName() + " | Units Available: " + stock.getQuantity();
    }
}

class Outlet {
    private int outletId;
    private String name;
    private String location;
    private String contactInfo;
    private List<Staff> staffMembers;
    private List<Inventory> inventories;

    public Outlet(int outletId, String name, String location, String contactInfo) {
        this.outletId = outletId;
        this.name = name;
        this.location = location;
        this.contactInfo = contactInfo;
        this.staffMembers = new ArrayList<>();
        this.inventories = new ArrayList<>();
    }

    public void addStaff(Staff staff) { staffMembers.add(staff); }
    public void addInventory(Inventory inventory) { inventories.add(inventory); }

    public void viewInventory() {
        System.out.println("Inventory for Outlet: " + name);
        for (Inventory inv : inventories) {
            System.out.println(" - " + inv.getStockDetails());
        }
    }
}

// ==========================================
// 5. DEMONSTRATION DRIVER (main)
// ==========================================

public class ElectronicShoppingSystem {
    public static void main(String[] args) {
        // Setup Catalog & Supply Chain
        Category electronics = new Category(101, "Electronics", "Gadgets and Devices");
        Seller primeSeller = new Seller(201, "TechHub Ltd", "contact@techhub.com");
        Supplier globalSupplier = new Supplier(301, "OmniSupply Corp", "sales@omnisupply.com", "42 Industrial Way");

        Product laptop = new Product(501, "Dell XPS 15", 1499.99, "High performance laptop", electronics, primeSeller, globalSupplier);
        Product mouse = new Product(502, "Logitech MX Master", 99.99, "Wireless ergonomic mouse", electronics, primeSeller, globalSupplier);

        // Setup Outlet & Inventory
        Outlet mainBranch = new Outlet(1, "Downtown Store", "5th Ave, NY", "212-555-0199");
        Stock laptopStock = new Stock(901, 20);
        Inventory laptopInventory = new Inventory(801, laptop, laptopStock);
        mainBranch.addInventory(laptopInventory);

        Staff technician = new Staff(701, "Alice Smith", "Support Specialist", "alice@store.com");
        mainBranch.addStaff(technician);

        // Customer Journey: Browse, Add to Cart, Purchase
        Customer customer = new Customer(1, "Jane Doe", "jane@example.com", "555-1234", "123 Elm St");
        customer.addToCart(laptop);
        customer.addToCart(mouse);

        System.out.println("\n--- Processing Order ---");
        Purchase purchase = customer.makePurchase(1001, "Credit Card");

        // After-Sales Service Request
        if (purchase != null) {
            System.out.println("\n--- Initiating After-Sales Service ---");
            AfterSalesService service = new AfterSalesService(4001, "Warranty Repair", purchase);
            service.raiseRequest();
            service.closeRequest("Screen replaced under warranty.");
        }
    }
}