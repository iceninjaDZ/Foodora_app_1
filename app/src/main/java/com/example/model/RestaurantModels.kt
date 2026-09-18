package com.example.model

enum class UserRole(val title: String) {
    OWNER("Owner"),
    MANAGER("Manager"),
    CASHIER("Cashier"),
    WAITER("Waiter"),
    KITCHEN("Kitchen"),
    DELIVERY("Delivery")
}

enum class Language(val code: String, val displayName: String, val isRtl: Boolean) {
    EN("en", "English", false),
    AR("ar", "العربية", true),
    FR("fr", "Français", false)
}

enum class BusinessType(val displayName: String) {
    RESTAURANT("Restaurant"),
    CAFE("Café & Bistro"),
    BAKERY("Bakery & Pastry"),
    FAST_FOOD("Fast Food & Grill"),
    FOOD_TRUCK("Food Truck")
}

enum class SaaSPlan(val displayName: String, val priceMonthly: String, val maxTables: Int, val maxStaff: Int) {
    STARTER("Starter", "$29/mo", 10, 5),
    PROFESSIONAL("Professional", "$79/mo", 35, 20),
    ENTERPRISE("Enterprise", "$199/mo", 999, 999)
}

data class BusinessProfile(
    val name: String,
    val type: BusinessType,
    val plan: SaaSPlan,
    val currencySymbol: String,
    val currencyCode: String,
    val taxRatePercent: Double,
    val serviceChargePercent: Double,
    val phone: String,
    val address: String,
    val branchesCount: Int = 2
) {
    fun formatCurrency(amount: Double): String {
        val formatted = if (amount % 1.0 == 0.0) {
            String.format(java.util.Locale.US, "%,.0f", amount)
        } else {
            String.format(java.util.Locale.US, "%,.2f", amount)
        }
        return if (currencySymbol.startsWith("$") || currencySymbol.startsWith("€") || currencySymbol.startsWith("£")) {
            "$currencySymbol$formatted"
        } else {
            "$formatted $currencySymbol"
        }
    }
}

data class Employee(
    val id: String,
    val name: String,
    val role: UserRole,
    val email: String,
    val phone: String,
    val isActive: Boolean,
    val pinCode: String,
    val shift: String
)

data class Category(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val nameFr: String,
    val iconName: String
)

data class MenuItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val nameFr: String,
    val categoryId: String,
    val price: Double,
    val costPrice: Double,
    val descriptionEn: String,
    val descriptionAr: String,
    val descriptionFr: String,
    val isAvailable: Boolean,
    val badge: String? = null,
    val prepTimeMinutes: Int = 12
)

data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int,
    val notes: String = ""
)

enum class OrderStatus {
    PENDING,
    PREPARING,
    READY,
    SERVED,
    OUT_FOR_DELIVERY,
    COMPLETED,
    CANCELLED
}

enum class OrderType {
    DINE_IN,
    TAKEOUT,
    DELIVERY
}

enum class PaymentMethod {
    CASH,
    CREDIT_CARD,
    DIGITAL_WALLET,
    UNPAID
}

data class Order(
    val id: String,
    val orderNumber: Int,
    val type: OrderType,
    val tableNumber: Int? = null,
    val customerName: String,
    val customerPhone: String = "",
    val deliveryAddress: String = "",
    val items: List<CartItem>,
    val subtotal: Double,
    val tax: Double,
    val discount: Double = 0.0,
    val total: Double,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val createdAtMillis: Long,
    val driverName: String? = null,
    val estimatedDeliveryMins: Int? = null
)

enum class TableStatus {
    AVAILABLE,
    OCCUPIED,
    RESERVED,
    BILL_REQUESTED
}

data class DiningTable(
    val id: String,
    val number: Int,
    val capacity: Int,
    val section: String, // "Indoor", "Patio", "VIP"
    val status: TableStatus,
    val activeOrderId: String? = null,
    val billAmount: Double = 0.0,
    val waiterName: String = "Sarah"
)

data class InventoryItem(
    val id: String,
    val name: String,
    val category: String,
    val currentStock: Double,
    val unit: String,
    val minThreshold: Double,
    val costPerUnit: Double,
    val supplier: String
)

data class Customer(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val totalOrders: Int,
    val totalSpent: Double,
    val loyaltyPoints: Int,
    val isVip: Boolean = false
)

data class Expense(
    val id: String,
    val title: String,
    val category: String,
    val amount: Double,
    val dateMillis: Long,
    val loggedBy: String
)

data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val timestampMillis: Long,
    val type: String, // "ORDER", "KDS", "STOCK", "DELIVERY"
    val isRead: Boolean = false
)

enum class ComparisonPeriod(val labelEn: String, val labelAr: String, val labelFr: String) {
    YESTERDAY("Vs. Yesterday", "مقارنة بالأمس", "Vs. Hier"),
    LAST_WEEK("Vs. Last Week", "مقارنة بالأسبوع الماضي", "Vs. Semaine passée"),
    MONTH_AVG("Vs. 30-Day Avg", "مقارنة بمتوسط 30 يوم", "Vs. Moyenne 30j")
}

data class HourlyTrendPoint(
    val hourLabel: String,
    val hour24: Int,
    val todayRevenue: Double,
    val previousPeriodRevenue: Double,
    val todayOrdersCount: Int,
    val previousOrdersCount: Int,
    val periodLabel: String = ""
)
