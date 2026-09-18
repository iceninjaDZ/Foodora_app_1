package com.example.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SampleData
import com.example.data.error.AppError
import com.example.data.error.ErrorCategory
import com.example.data.error.GlobalExceptionHandler
import com.example.data.error.SyncStatus
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Screen {
    DASHBOARD,
    POS,
    KDS,
    TABLES,
    MENU,
    INVENTORY,
    DELIVERY,
    CRM,
    REPORTS,
    ANALYTICS,
    STAFF,
    QR_MENU,
    SETTINGS
}

data class AuthUser(
    val username: String,
    val name: String,
    val role: UserRole,
    val email: String,
    val avatarUrl: String = ""
)

class FoodoraViewModel : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<AuthUser?>(
        AuthUser(
            username = "manager@foodora.io",
            name = "Karim Benali (Manager)",
            role = UserRole.MANAGER,
            email = "manager@foodora.io"
        )
    )
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.OWNER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentLanguage = MutableStateFlow(Language.AR)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _businessProfile = MutableStateFlow(SampleData.initialProfile)
    val businessProfile: StateFlow<BusinessProfile> = _businessProfile.asStateFlow()

    private val _categories = MutableStateFlow(SampleData.categories)
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _menuItems = MutableStateFlow(SampleData.menuItems)
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val _orders = MutableStateFlow(SampleData.initialOrders)
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _diningTables = MutableStateFlow(SampleData.diningTables)
    val diningTables: StateFlow<List<DiningTable>> = _diningTables.asStateFlow()

    private val _inventory = MutableStateFlow(SampleData.inventoryItems)
    val inventory: StateFlow<List<InventoryItem>> = _inventory.asStateFlow()

    private val _customers = MutableStateFlow(SampleData.customers)
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    private val _employees = MutableStateFlow(SampleData.employees)
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    private val _expenses = MutableStateFlow(SampleData.expenses)
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _notifications = MutableStateFlow(SampleData.notifications)
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Global Exception, API & Firebase Sync Management
    private val _currentError = MutableStateFlow<AppError?>(null)
    val currentError: StateFlow<AppError?> = _currentError.asStateFlow()

    private val _syncStatus = MutableStateFlow(SyncStatus.SYNCED)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTimestamp: StateFlow<Long> = _lastSyncTimestamp.asStateFlow()

    private val _errorHistory = MutableStateFlow<List<AppError>>(emptyList())
    val errorHistory: StateFlow<List<AppError>> = _errorHistory.asStateFlow()

    private var lastFailedAction: (suspend () -> Unit)? = null

    // Daily Sales Analytics & Recharts Comparison
    private val _selectedComparisonPeriod = MutableStateFlow(ComparisonPeriod.YESTERDAY)
    val selectedComparisonPeriod: StateFlow<ComparisonPeriod> = _selectedComparisonPeriod.asStateFlow()

    private val _hourlySalesTrends = MutableStateFlow(SampleData.getHourlySalesTrends(ComparisonPeriod.YESTERDAY))
    val hourlySalesTrends: StateFlow<List<HourlyTrendPoint>> = _hourlySalesTrends.asStateFlow()

    fun selectComparisonPeriod(period: ComparisonPeriod) {
        _selectedComparisonPeriod.value = period
        _hourlySalesTrends.value = SampleData.getHourlySalesTrends(period)
    }

    // POS Cart State
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _cartOrderType = MutableStateFlow(OrderType.DINE_IN)
    val cartOrderType: StateFlow<OrderType> = _cartOrderType.asStateFlow()

    private val _cartTableNumber = MutableStateFlow<Int?>(2)
    val cartTableNumber: StateFlow<Int?> = _cartTableNumber.asStateFlow()

    private val _cartCustomerName = MutableStateFlow("Guest Customer")
    val cartCustomerName: StateFlow<String> = _cartCustomerName.asStateFlow()

    private var nextOrderNumber = 106

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun switchRole(role: UserRole) {
        _currentRole.value = role
        // Suggest a friendly default landing screen per role
        when (role) {
            UserRole.KITCHEN -> _currentScreen.value = Screen.KDS
            UserRole.CASHIER -> _currentScreen.value = Screen.POS
            UserRole.WAITER -> _currentScreen.value = Screen.TABLES
            UserRole.DELIVERY -> _currentScreen.value = Screen.DELIVERY
            UserRole.OWNER, UserRole.MANAGER -> Unit // Keep screen
        }
    }

    fun setLanguage(language: Language) {
        _currentLanguage.value = language
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setCurrency(symbol: String, code: String) {
        _businessProfile.update { it.copy(currencySymbol = symbol, currencyCode = code) }
    }

    fun updatePlan(plan: SaaSPlan) {
        _businessProfile.update { it.copy(plan = plan) }
    }

    // POS actions
    fun addToCart(item: MenuItem) {
        _cart.update { current ->
            val index = current.indexOfFirst { it.menuItem.id == item.id }
            if (index >= 0) {
                current.toMutableList().also { list ->
                    list[index] = list[index].copy(quantity = list[index].quantity + 1)
                }
            } else {
                current + CartItem(item, 1)
            }
        }
    }

    fun increaseCartItem(item: MenuItem) {
        addToCart(item)
    }

    fun decreaseCartItem(item: MenuItem) {
        _cart.update { current ->
            val index = current.indexOfFirst { it.menuItem.id == item.id }
            if (index >= 0) {
                val existing = current[index]
                if (existing.quantity > 1) {
                    current.toMutableList().also { list ->
                        list[index] = existing.copy(quantity = existing.quantity - 1)
                    }
                } else {
                    current.filter { it.menuItem.id != item.id }
                }
            } else {
                current
            }
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun setCartOrderType(type: OrderType) {
        _cartOrderType.value = type
        if (type != OrderType.DINE_IN) {
            _cartTableNumber.value = null
        } else if (_cartTableNumber.value == null) {
            _cartTableNumber.value = 1
        }
    }

    fun setCartTableNumber(tableNum: Int?) {
        _cartTableNumber.value = tableNum
    }

    fun setCartCustomerName(name: String) {
        _cartCustomerName.value = name
    }

    fun checkoutCart(paymentMethod: PaymentMethod): Order? {
        val currentItems = _cart.value
        if (currentItems.isEmpty()) return null

        val subtotal = currentItems.sumOf { it.menuItem.price * it.quantity }
        val tax = subtotal * (_businessProfile.value.taxRatePercent / 100.0)
        val total = subtotal + tax

        val orderNum = nextOrderNumber++
        val orderType = _cartOrderType.value
        val tableNum = if (orderType == OrderType.DINE_IN) _cartTableNumber.value else null
        val customer = _cartCustomerName.value.ifBlank { "Guest #${orderNum}" }

        val newOrder = Order(
            id = "ord_$orderNum",
            orderNumber = orderNum,
            type = orderType,
            tableNumber = tableNum,
            customerName = customer,
            items = currentItems,
            subtotal = subtotal,
            tax = tax,
            total = total,
            paymentMethod = paymentMethod,
            status = OrderStatus.PENDING,
            createdAtMillis = System.currentTimeMillis()
        )

        _orders.update { listOf(newOrder) + it }

        // If table order, mark table occupied
        if (tableNum != null) {
            _diningTables.update { tables ->
                tables.map { tbl ->
                    if (tbl.number == tableNum) {
                        tbl.copy(status = TableStatus.OCCUPIED, activeOrderId = newOrder.id, billAmount = total)
                    } else tbl
                }
            }
        }

        // Add Notification
        _notifications.update { current ->
            listOf(
                NotificationItem(
                    id = "n_${System.currentTimeMillis()}",
                    title = "New Order #$orderNum Placed",
                    description = "$orderType • $customer • Total: ${_businessProfile.value.formatCurrency(total)}",
                    timestampMillis = System.currentTimeMillis(),
                    type = "ORDER"
                )
            ) + current
        }

        clearCart()
        return newOrder
    }

    // KDS order bump
    fun advanceOrderStatus(orderId: String) {
        _orders.update { ordersList ->
            ordersList.map { ord ->
                if (ord.id == orderId) {
                    val nextStatus = when (ord.status) {
                        OrderStatus.PENDING -> OrderStatus.PREPARING
                        OrderStatus.PREPARING -> OrderStatus.READY
                        OrderStatus.READY -> {
                            if (ord.type == OrderType.DELIVERY) OrderStatus.OUT_FOR_DELIVERY
                            else OrderStatus.SERVED
                        }
                        OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.COMPLETED
                        OrderStatus.SERVED -> OrderStatus.COMPLETED
                        OrderStatus.COMPLETED -> OrderStatus.COMPLETED
                        OrderStatus.CANCELLED -> OrderStatus.CANCELLED
                    }
                    ord.copy(status = nextStatus)
                } else ord
            }
        }
    }

    fun cancelOrder(orderId: String) {
        _orders.update { ordersList ->
            ordersList.map { ord ->
                if (ord.id == orderId) ord.copy(status = OrderStatus.CANCELLED) else ord
            }
        }
    }

    // Tables
    fun updateTableStatus(tableId: String, status: TableStatus) {
        _diningTables.update { tables ->
            tables.map { t ->
                if (t.id == tableId) {
                    val bill = if (status == TableStatus.AVAILABLE) 0.0 else t.billAmount
                    val orderId = if (status == TableStatus.AVAILABLE) null else t.activeOrderId
                    t.copy(status = status, billAmount = bill, activeOrderId = orderId)
                } else t
            }
        }
    }

    // Menu
    fun toggleMenuItemAvailability(itemId: String) {
        _menuItems.update { items ->
            items.map { item ->
                if (item.id == itemId) item.copy(isAvailable = !item.isAvailable) else item
            }
        }
    }

    fun addMenuItem(item: MenuItem) {
        _menuItems.update { it + item }
    }

    fun updateMenuItem(updatedItem: MenuItem) {
        _menuItems.update { items ->
            items.map { if (it.id == updatedItem.id) updatedItem else it }
        }
    }

    fun deleteMenuItem(itemId: String) {
        _menuItems.update { items -> items.filter { it.id != itemId } }
    }

    fun addCategory(category: Category) {
        _categories.update { it + category }
    }

    fun updateCategory(updatedCategory: Category) {
        _categories.update { cats ->
            cats.map { if (it.id == updatedCategory.id) updatedCategory else it }
        }
    }

    fun deleteCategory(categoryId: String) {
        _categories.update { cats -> cats.filter { it.id != categoryId } }
    }

    // Inventory
    fun adjustStock(itemId: String, delta: Double) {
        _inventory.update { items ->
            items.map { inv ->
                if (inv.id == itemId) {
                    val updated = (inv.currentStock + delta).coerceAtLeast(0.0)
                    inv.copy(currentStock = updated)
                } else inv
            }
        }
    }

    fun addInventoryItem(item: InventoryItem) {
        _inventory.update { it + item }
    }

    fun updateInventoryItem(updatedItem: InventoryItem) {
        _inventory.update { items ->
            items.map { if (it.id == updatedItem.id) updatedItem else it }
        }
    }

    fun deleteInventoryItem(itemId: String) {
        _inventory.update { items -> items.filter { it.id != itemId } }
    }

    // Tables Management
    fun addDiningTable(table: DiningTable) {
        _diningTables.update { it + table }
    }

    fun updateDiningTable(updatedTable: DiningTable) {
        _diningTables.update { tables ->
            tables.map { if (it.id == updatedTable.id) updatedTable else it }
        }
    }

    fun deleteDiningTable(tableId: String) {
        _diningTables.update { tables -> tables.filter { it.id != tableId } }
    }

    // Staff
    fun addEmployee(employee: Employee) {
        _employees.update { it + employee }
    }

    fun updateEmployee(updatedEmployee: Employee) {
        _employees.update { employees ->
            employees.map { if (it.id == updatedEmployee.id) updatedEmployee else it }
        }
    }

    fun deleteEmployee(employeeId: String) {
        _employees.update { employees -> employees.filter { it.id != employeeId } }
    }

    // Delivery
    fun dispatchDeliveryOrder(orderId: String, driver: String) {
        _orders.update { ordersList ->
            ordersList.map { ord ->
                if (ord.id == orderId) {
                    ord.copy(
                        status = OrderStatus.OUT_FOR_DELIVERY,
                        driverName = driver,
                        estimatedDeliveryMins = 20
                    )
                } else ord
            }
        }
    }

    fun markOrderDelivered(orderId: String) {
        _orders.update { ordersList ->
            ordersList.map { ord ->
                if (ord.id == orderId) {
                    ord.copy(status = OrderStatus.COMPLETED)
                } else ord
            }
        }
    }

    fun updateBusinessProfile(profile: BusinessProfile) {
        _businessProfile.value = profile
    }

    // CRM
    fun addCustomer(customer: Customer) {
        _customers.update { listOf(customer) + it }
    }

    fun updateCustomer(updatedCustomer: Customer) {
        _customers.update { customers ->
            customers.map { if (it.id == updatedCustomer.id) updatedCustomer else it }
        }
    }

    fun deleteCustomer(customerId: String) {
        _customers.update { customers -> customers.filter { it.id != customerId } }
    }

    // Expenses
    fun addExpense(expense: Expense) {
        _expenses.update { listOf(expense) + it }
    }

    fun updateExpense(updatedExpense: Expense) {
        _expenses.update { list ->
            list.map { if (it.id == updatedExpense.id) updatedExpense else it }
        }
    }

    fun deleteExpense(expenseId: String) {
        _expenses.update { list ->
            list.filter { it.id != expenseId }
        }
    }

    // Authentication
    fun login(role: UserRole, username: String = "") {
        val user = when (role) {
            UserRole.OWNER -> AuthUser(
                username = username.ifBlank { "admin@foodora.io" },
                name = "Alexandre Dumas (Super Admin)",
                role = UserRole.OWNER,
                email = "admin@foodora.io"
            )
            UserRole.MANAGER -> AuthUser(
                username = username.ifBlank { "manager@foodora.io" },
                name = "Karim Benali (General Manager)",
                role = UserRole.MANAGER,
                email = "manager@foodora.io"
            )
            UserRole.CASHIER -> AuthUser(
                username = username.ifBlank { "cashier@foodora.io" },
                name = "Amina Kaci (Cashier #1)",
                role = UserRole.CASHIER,
                email = "cashier@foodora.io"
            )
            UserRole.KITCHEN -> AuthUser(
                username = username.ifBlank { "kitchen@foodora.io" },
                name = "Chef Marco (Head Chef)",
                role = UserRole.KITCHEN,
                email = "kitchen@foodora.io"
            )
            UserRole.WAITER -> AuthUser(
                username = username.ifBlank { "waiter@foodora.io" },
                name = "Sarah Mansouri (Floor Waiter)",
                role = UserRole.WAITER,
                email = "waiter@foodora.io"
            )
            UserRole.DELIVERY -> AuthUser(
                username = username.ifBlank { "delivery@foodora.io" },
                name = "Yacine Cherif (Fleet Driver)",
                role = UserRole.DELIVERY,
                email = "delivery@foodora.io"
            )
        }
        _currentUser.value = user
        _currentRole.value = role
        _isLoggedIn.value = true

        // Navigate to optimal screen per role
        when (role) {
            UserRole.KITCHEN -> _currentScreen.value = Screen.KDS
            UserRole.CASHIER -> _currentScreen.value = Screen.POS
            UserRole.WAITER -> _currentScreen.value = Screen.TABLES
            UserRole.DELIVERY -> _currentScreen.value = Screen.DELIVERY
            UserRole.OWNER, UserRole.MANAGER -> _currentScreen.value = Screen.DASHBOARD
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
        _currentScreen.value = Screen.DASHBOARD
    }

    // Notifications
    fun markNotificationRead(id: String) {
        _notifications.update { items ->
            items.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun markAllNotificationsRead() {
        _notifications.update { items ->
            items.map { it.copy(isRead = true) }
        }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    // Global Exception Handling & Cloud Synchronization
    fun dismissError() {
        _currentError.value = null
    }

    fun handleException(
        throwable: Throwable,
        contextInfo: String = "App Operation",
        onRetry: (suspend () -> Unit)? = null
    ) {
        lastFailedAction = onRetry
        val error = GlobalExceptionHandler.parseException(
            throwable = throwable,
            contextInfo = contextInfo,
            language = _currentLanguage.value
        )
        _currentError.value = error
        _syncStatus.value = if (error.category == ErrorCategory.NETWORK_OFFLINE) SyncStatus.OFFLINE else SyncStatus.FAILED
        _errorHistory.update { listOf(error) + it.take(25) }
    }

    fun retryLastFailedOperation() {
        val action = lastFailedAction
        _currentError.value = null
        if (action != null) {
            viewModelScope.launch {
                action.invoke()
            }
        } else {
            triggerCloudSync()
        }
    }

    fun triggerCloudSync() {
        if (_isSyncing.value) return
        _isSyncing.value = true
        _syncStatus.value = SyncStatus.SYNCING

        viewModelScope.launch {
            val result = GlobalExceptionHandler.safeFirebaseCall(
                operationName = "Firestore Operations Sync",
                language = _currentLanguage.value,
                onError = { appError ->
                    _currentError.value = appError
                    _syncStatus.value = SyncStatus.FAILED
                    _errorHistory.update { listOf(appError) + it.take(25) }
                }
            ) {
                delay(1200)
                _lastSyncTimestamp.value = System.currentTimeMillis()
                true
            }

            if (result.isSuccess) {
                _syncStatus.value = SyncStatus.SYNCED
                _currentError.value = null
            }
            _isSyncing.value = false
        }
    }

    fun simulateException(category: ErrorCategory) {
        val simulatedThrowable: Throwable = when (category) {
            ErrorCategory.NETWORK_OFFLINE -> java.net.UnknownHostException("Unable to resolve host 'foodora-cloud.firebaseio.com': No address associated with hostname")
            ErrorCategory.NETWORK_TIMEOUT -> java.net.SocketTimeoutException("Read timed out after 10000ms while syncing orders with api.foodora.io")
            ErrorCategory.FIREBASE_AUTH -> com.google.firebase.auth.FirebaseAuthException("ERROR_INVALID_CREDENTIAL", "The Firebase user token is expired or revoked by security rules.")
            ErrorCategory.FIREBASE_FIRESTORE_SYNC -> com.google.firebase.firestore.FirebaseFirestoreException("The Cloud Firestore service is temporarily unavailable in this cluster.", com.google.firebase.firestore.FirebaseFirestoreException.Code.UNAVAILABLE)
            ErrorCategory.FIREBASE_QUOTA -> com.google.firebase.firestore.FirebaseFirestoreException("Cloud Firestore quota exceeded. Limit is 50,000 writes/day.", com.google.firebase.firestore.FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED)
            ErrorCategory.SERVER_ERROR -> retrofit2.HttpException(retrofit2.Response.error<Any>(503, okhttp3.ResponseBody.create(null, "Gateway Timeout / Service Unavailable")))
            ErrorCategory.CLIENT_ERROR -> retrofit2.HttpException(retrofit2.Response.error<Any>(400, okhttp3.ResponseBody.create(null, "Bad Request: Invalid Payload")))
            ErrorCategory.UNKNOWN -> RuntimeException("Unexpected internal exception in sync dispatcher")
        }

        handleException(
            throwable = simulatedThrowable,
            contextInfo = "Test: ${category.name}",
            onRetry = {
                triggerCloudSync()
            }
        )
    }

    fun clearErrorHistory() {
        _errorHistory.value = emptyList()
    }
}
