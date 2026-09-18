package com.example.data

import com.example.model.*

object SampleData {
    val initialProfile = BusinessProfile(
        name = "Foodora Bistro & Grill Alger",
        type = BusinessType.RESTAURANT,
        plan = SaaSPlan.PROFESSIONAL,
        currencySymbol = "د.ج",
        currencyCode = "DZD",
        taxRatePercent = 9.0,
        serviceChargePercent = 5.0,
        phone = "+213 (0) 23 45 67 89",
        address = "14 Rue Didouche Mourad, Alger Centre",
        branchesCount = 2
    )

    val categories = listOf(
        Category("cat_1", "Burgers & Grill", "البرجر والمشاوي", "Burgers & Grillades", "fastfood"),
        Category("cat_2", "Artisan Pizzas", "بيتزا إيطالية", "Pizzas Artisanales", "local_pizza"),
        Category("cat_3", "Café & Bakery", "المقهى والمخبوزات", "Café & Pâtisserie", "local_cafe"),
        Category("cat_4", "Fresh Bowls", "أطباق وسلطات", "Bowls & Salades", "restaurant"),
        Category("cat_5", "Beverages", "المشروبات والعصائر", "Boissons & Jus", "local_bar"),
        Category("cat_6", "Desserts", "الحلويات", "Desserts Gourmands", "cake")
    )

    val menuItems = listOf(
        MenuItem(
            id = "m_1",
            nameEn = "Truffle Smash Burger",
            nameAr = "برجر الكمأة المشوي",
            nameFr = "Smash Burger à la Truffe",
            categoryId = "cat_1",
            price = 1200.0,
            costPrice = 450.0,
            descriptionEn = "Double Angus patty, melted aged cheddar, black truffle aioli, caramelized shallots on brioche.",
            descriptionAr = "شريحتان من لحم الأنجوس مع جبن الشيدر المعتق وصوص الكمأة الأسود في خبز البريوش الفاخر.",
            descriptionFr = "Double steak Angus, cheddar affiné fondu, aïoli à la truffe noire et échalotes confites.",
            isAvailable = true,
            badge = "Bestseller",
            prepTimeMinutes = 10
        ),
        MenuItem(
            id = "m_2",
            nameEn = "Crispy Chicken Avocado",
            nameAr = "دجاج مقرمش بالأفوكادو",
            nameFr = "Poulet Croustillant Avocat",
            categoryId = "cat_1",
            price = 950.0,
            costPrice = 380.0,
            descriptionEn = "Buttermilk fried chicken breast, fresh hass avocado, spicy chipotle mayo, coleslaw.",
            descriptionAr = "صدر دجاج مقلي ومقرمش مع شرائح الأفوكادو الطازجة وصلصة الشيبوتلي الحارة.",
            descriptionFr = "Filet de poulet croustillant mariné au babeurre, avocat frais, mayo chipotle relevée.",
            isAvailable = true,
            badge = "Popular",
            prepTimeMinutes = 12
        ),
        MenuItem(
            id = "m_3",
            nameEn = "Margherita D.O.P.",
            nameAr = "بيتزا مارغريتا نابولية",
            nameFr = "Margherita D.O.P.",
            categoryId = "cat_2",
            price = 850.0,
            costPrice = 280.0,
            descriptionEn = "San Marzano tomatoes, fresh buffalo mozzarella, fragrant basil, cold-pressed olive oil.",
            descriptionAr = "طماطم سان مارزانو الإيطالية، جبنة موزاريلا الجاموس، ريحان طازج وزيت زيتون بكر.",
            descriptionFr = "Tomates San Marzano, mozzarella di bufala fraîche, basilic parfumé et huile d'olive vierge.",
            isAvailable = true,
            badge = "Chef Choice",
            prepTimeMinutes = 8
        ),
        MenuItem(
            id = "m_4",
            nameEn = "Spicy Diavola Pizza",
            nameAr = "بيتزا ديافولا الحارة",
            nameFr = "Pizza Diavola Piquante",
            categoryId = "cat_2",
            price = 1100.0,
            costPrice = 390.0,
            descriptionEn = "Calabrian spicy soppressata, chili flakes, roasted red peppers, fior di latte.",
            descriptionAr = "سلامي كالابريا حار، رقائق الفلفل الحار، فلفل أحمر مشوي وجبنة فيور دي لاتي.",
            descriptionFr = "Spianata piquante de Calabre, piments, poivrons rôtis et fior di latte fondant.",
            isAvailable = true,
            prepTimeMinutes = 9
        ),
        MenuItem(
            id = "m_5",
            nameEn = "Almond Butter Croissant",
            nameAr = "كرواسون اللوز بالزبدة",
            nameFr = "Croissant aux Amandes",
            categoryId = "cat_3",
            price = 280.0,
            costPrice = 90.0,
            descriptionEn = "Flaky artisanal French pastry filled with rich frangipane cream and toasted sliced almonds.",
            descriptionAr = "معجنات فرنسية هشة ومقرمشة محشوة بكريمة اللوز الغنية ورقائق اللوز المحمص.",
            descriptionFr = "Feuilletage pur beurre doré garni d'une crème frangipane onctueuse et amandes effilées.",
            isAvailable = true,
            badge = "Bakery Fresh",
            prepTimeMinutes = 2
        ),
        MenuItem(
            id = "m_6",
            nameEn = "Spanish Cortado Latte",
            nameAr = "كورتادو إسباني",
            nameFr = "Cortado Espagnol",
            categoryId = "cat_3",
            price = 220.0,
            costPrice = 60.0,
            descriptionEn = "Equal parts double espresso blend and warm silky textured milk with a touch of condensed milk.",
            descriptionAr = "مزيج متساوي من إسبريسو مزدوج وحليب مبخر مخملي مع لمسة حليب مكثف.",
            descriptionFr = "Double expresso équilibré par une dose égale de lait chaud soyeux et douceur subtile.",
            isAvailable = true,
            prepTimeMinutes = 3
        ),
        MenuItem(
            id = "m_7",
            nameEn = "Quinoa Salmon Poke Bowl",
            nameAr = "وعاء السلمون والكينوا",
            nameFr = "Poke Bowl Saumon Quinoa",
            categoryId = "cat_4",
            price = 1650.0,
            costPrice = 620.0,
            descriptionEn = "Sashimi Atlantic salmon, tri-color quinoa, edamame, cucumber ribbons, ponzu sesame vinaigrette.",
            descriptionAr = "سلمون أطلسي طازج، كينوا ملونة، إدامامي، خيار وصوص البونزو والسمسم الخاص.",
            descriptionFr = "Saumon frais mariné, quinoa tri-couleur, edamame, lamelles de concombre, sauce ponzu sésame.",
            isAvailable = true,
            badge = "Healthy",
            prepTimeMinutes = 7
        ),
        MenuItem(
            id = "m_8",
            nameEn = "Passion Fruit Matcha Spritz",
            nameAr = "ماتشا باشن فروت فوار",
            nameFr = "Matcha Spritz Fruit de la Passion",
            categoryId = "cat_5",
            price = 450.0,
            costPrice = 120.0,
            descriptionEn = "Ceremonial grade Uji matcha, natural passion fruit puree, sparkling mineral water, fresh mint.",
            descriptionAr = "ماتشا أوجي يابانية، مهروس فاكهة الباشن فروت الطبيعية، مياه فوارة ونعناع طازج.",
            descriptionFr = "Matcha cérémonial japonais, purée de fruit de la passion, eau pétillante et menthe fraîche.",
            isAvailable = true,
            prepTimeMinutes = 4
        ),
        MenuItem(
            id = "m_9",
            nameEn = "Pistachio Tiramisu Cup",
            nameAr = "تيراميسو الفستق الحلبي",
            nameFr = "Tiramisu Pistache d'Italie",
            categoryId = "cat_6",
            price = 550.0,
            costPrice = 180.0,
            descriptionEn = "Savoiardi ladyfingers steeped in espresso, Bronte pistachio mascarpone mousse, crushed pistachios.",
            descriptionAr = "بسكويت ليدي فينجر منقوع بالإسبريسو مع موس ماسكاربوني فستق برونتي الفاخر.",
            descriptionFr = "Biscuits cuillère imbibés d'expresso, crème onctueuse mascarpone et pistaches grillées.",
            isAvailable = true,
            badge = "Dessert Star",
            prepTimeMinutes = 3
        )
    )

    val diningTables = listOf(
        DiningTable("t_1", 1, 2, "Indoor", TableStatus.AVAILABLE, null, 0.0, "Alex"),
        DiningTable("t_2", 2, 4, "Indoor", TableStatus.OCCUPIED, "ord_101", 1798.5, "Alex"),
        DiningTable("t_3", 3, 4, "Indoor", TableStatus.BILL_REQUESTED, "ord_102", 3052.0, "Sarah"),
        DiningTable("t_4", 4, 6, "Indoor", TableStatus.AVAILABLE, null, 0.0, "Sarah"),
        DiningTable("t_5", 5, 2, "Patio", TableStatus.OCCUPIED, "ord_103", 1340.7, "Karim"),
        DiningTable("t_6", 6, 4, "Patio", TableStatus.RESERVED, null, 0.0, "Karim"),
        DiningTable("t_7", 7, 6, "Patio", TableStatus.AVAILABLE, null, 0.0, "Karim"),
        DiningTable("t_8", 8, 8, "VIP", TableStatus.OCCUPIED, "ord_104", 4414.5, "Elena"),
        DiningTable("t_9", 9, 4, "VIP", TableStatus.AVAILABLE, null, 0.0, "Elena")
    )

    val initialOrders = listOf(
        Order(
            id = "ord_101",
            orderNumber = 101,
            type = OrderType.DINE_IN,
            tableNumber = 2,
            customerName = "David Miller",
            items = listOf(
                CartItem(menuItems[0], 1, "Well done, extra pickles"),
                CartItem(menuItems[7], 1)
            ),
            subtotal = 1650.0,
            tax = 148.5,
            total = 1798.5,
            paymentMethod = PaymentMethod.UNPAID,
            status = OrderStatus.PREPARING,
            createdAtMillis = System.currentTimeMillis() - 8 * 60 * 1000
        ),
        Order(
            id = "ord_102",
            orderNumber = 102,
            type = OrderType.DINE_IN,
            tableNumber = 3,
            customerName = "Nadia Benali",
            items = listOf(
                CartItem(menuItems[2], 2),
                CartItem(menuItems[8], 2)
            ),
            subtotal = 2800.0,
            tax = 252.0,
            total = 3052.0,
            paymentMethod = PaymentMethod.CREDIT_CARD,
            status = OrderStatus.READY,
            createdAtMillis = System.currentTimeMillis() - 18 * 60 * 1000
        ),
        Order(
            id = "ord_103",
            orderNumber = 103,
            type = OrderType.TAKEOUT,
            tableNumber = null,
            customerName = "Marcus Vance",
            customerPhone = "+213 550 12 34 56",
            items = listOf(
                CartItem(menuItems[1], 1),
                CartItem(menuItems[4], 1)
            ),
            subtotal = 1230.0,
            tax = 110.7,
            total = 1340.7,
            paymentMethod = PaymentMethod.DIGITAL_WALLET,
            status = OrderStatus.PENDING,
            createdAtMillis = System.currentTimeMillis() - 3 * 60 * 1000
        ),
        Order(
            id = "ord_104",
            orderNumber = 104,
            type = OrderType.DELIVERY,
            tableNumber = null,
            customerName = "Sophie Dubois",
            customerPhone = "+213 661 78 90 12",
            deliveryAddress = "45 Rue Didouche Mourad, Alger",
            items = listOf(
                CartItem(menuItems[0], 2),
                CartItem(menuItems[3], 1),
                CartItem(menuItems[8], 1)
            ),
            subtotal = 4050.0,
            tax = 364.5,
            total = 4414.5,
            paymentMethod = PaymentMethod.DIGITAL_WALLET,
            status = OrderStatus.OUT_FOR_DELIVERY,
            createdAtMillis = System.currentTimeMillis() - 28 * 60 * 1000,
            driverName = "Tariq Mansour",
            estimatedDeliveryMins = 12
        ),
        Order(
            id = "ord_105",
            orderNumber = 105,
            type = OrderType.DELIVERY,
            tableNumber = null,
            customerName = "Amine Larbi",
            customerPhone = "+213 770 45 67 89",
            deliveryAddress = "12 Boulevard Zighoud Youcef, Alger",
            items = listOf(
                CartItem(menuItems[6], 2),
                CartItem(menuItems[7], 2)
            ),
            subtotal = 4200.0,
            tax = 378.0,
            total = 4578.0,
            paymentMethod = PaymentMethod.CASH,
            status = OrderStatus.READY,
            createdAtMillis = System.currentTimeMillis() - 14 * 60 * 1000,
            driverName = "Tariq Mansour",
            estimatedDeliveryMins = 20
        )
    )

    val inventoryItems = listOf(
        InventoryItem("inv_1", "Angus Beef Patties", "Meat & Poultry", 24.0, "kg", 15.0, 1800.0, "Mitidja Farms Co."),
        InventoryItem("inv_2", "Buffalo Mozzarella D.O.P.", "Dairy & Cheese", 4.2, "kg", 8.0, 1500.0, "Lombardia Imports"),
        InventoryItem("inv_3", "San Marzano Canned Tomatoes", "Pantry", 36.0, "cans", 20.0, 320.0, "Italian Goods Ltd."),
        InventoryItem("inv_4", "Brioche Burger Buns", "Bakery", 18.0, "packs", 25.0, 250.0, "Golden Crust Bakery"),
        InventoryItem("inv_5", "Black Truffle Aioli", "Sauces", 2.1, "L", 5.0, 2200.0, "Gourmet Flavors Co."),
        InventoryItem("inv_6", "Fresh Atlantic Salmon", "Seafood", 8.5, "kg", 5.0, 2800.0, "Nordic Ocean Catch"),
        InventoryItem("inv_7", "Hass Avocados", "Produce", 45.0, "pcs", 30.0, 180.0, "Tipaza Fresh Farms"),
        InventoryItem("inv_8", "Japanese Uji Matcha Powder", "Beverages", 1.8, "kg", 1.0, 4500.0, "Kyoto Tea Exporters"),
        InventoryItem("inv_9", "Fresh Local Whole Milk", "Dairy", 38.0, "L", 20.0, 130.0, "Soummam Dairy Co.")
    )

    val customers = listOf(
        Customer("c_1", "Nadia Benali", "+213 555 43 28 81", "nadia.b@example.com", 24, 68500.0, 420, true),
        Customer("c_2", "David Miller", "+213 661 91 23 40", "david.m@example.com", 14, 34500.0, 210, false),
        Customer("c_3", "Sophie Dubois", "+213 770 48 21 92", "sophie.dubois@example.com", 32, 114000.0, 890, true),
        Customer("c_4", "Marcus Vance", "+213 550 01 92 83", "mvance@example.com", 8, 16400.0, 95, false),
        Customer("c_5", "Amina Al-Mansoor", "+213 662 76 29 90", "amina.mansoor@example.com", 19, 53000.0, 340, true)
    )

    val employees = listOf(
        Employee("emp_1", "Hassan El-Khatib", UserRole.OWNER, "hassan@foodora.dz", "+213 550 01 01 01", true, "1100", "All Shifts"),
        Employee("emp_2", "Claire Moreau", UserRole.MANAGER, "claire.m@foodora.dz", "+213 550 01 02 02", true, "2200", "Morning / Shift Lead"),
        Employee("emp_3", "Leo Rossi", UserRole.CASHIER, "leo.r@foodora.dz", "+213 550 01 03 03", true, "3300", "Day Shift (8am - 4pm)"),
        Employee("emp_4", "Sarah Jenkins", UserRole.WAITER, "sarah.j@foodora.dz", "+213 550 01 04 04", true, "4400", "Evening Floor"),
        Employee("emp_5", "Chef Marco Bellini", UserRole.KITCHEN, "marco.b@foodora.dz", "+213 550 01 05 05", true, "5500", "Hot Line / Grill Master"),
        Employee("emp_6", "Tariq Mansour", UserRole.DELIVERY, "tariq.m@foodora.dz", "+213 550 01 06 06", true, "6600", "Fleet Delivery 1")
    )

    val expenses = listOf(
        Expense("exp_1", "Fresh Produce & Meat Delivery", "Ingredients", 42000.00, System.currentTimeMillis() - 6 * 3600 * 1000, "Chef Marco"),
        Expense("exp_2", "Kitchen Utility & Gas Refill", "Utilities", 14500.00, System.currentTimeMillis() - 14 * 3600 * 1000, "Claire Moreau"),
        Expense("exp_3", "Eco Packaging & Paper Bags", "Supplies", 8500.00, System.currentTimeMillis() - 24 * 3600 * 1000, "Leo Rossi"),
        Expense("exp_4", "Social Media Ads Campaign", "Marketing", 12000.00, System.currentTimeMillis() - 48 * 3600 * 1000, "Hassan El-Khatib"),
        Expense("exp_5", "Espresso Machine Maintenance", "Equipment", 9500.00, System.currentTimeMillis() - 72 * 3600 * 1000, "Claire Moreau")
    )

    val notifications = listOf(
        NotificationItem("n_1", "طلب توصيل جديد #105", "الزبون أمين العربي طلب 4 عناصر (4,578 د.ج)", System.currentTimeMillis() - 14 * 60 * 1000, "DELIVERY"),
        NotificationItem("n_2", "تنبيه مخزون: جبن الموزاريلا", "المتبقي: 4.2 كغ (الحد الأدنى: 8.0 كغ)", System.currentTimeMillis() - 35 * 60 * 1000, "STOCK"),
        NotificationItem("n_3", "تذكرة المطبخ #102 جاهزة", "طلب الطاولة رقم 3 جاهز للتسليم", System.currentTimeMillis() - 50 * 60 * 1000, "KDS"),
        NotificationItem("n_4", "الوصول إلى هدف المبيعات اليومي", "تجاوزت المبيعات اليوم 120,000 د.ج", System.currentTimeMillis() - 120 * 60 * 1000, "ORDER")
    )

    fun getHourlySalesTrends(period: ComparisonPeriod): List<HourlyTrendPoint> {
        return when (period) {
            ComparisonPeriod.YESTERDAY -> listOf(
                HourlyTrendPoint("08:00", 8, 3500.0, 2800.0, 3, 2, "Breakfast Rush"),
                HourlyTrendPoint("09:00", 9, 6200.0, 5100.0, 5, 4, "Morning Coffee"),
                HourlyTrendPoint("10:00", 10, 8400.0, 7800.0, 6, 6, "Bakery Pickups"),
                HourlyTrendPoint("11:00", 11, 14200.0, 11500.0, 9, 8, "Early Lunch"),
                HourlyTrendPoint("12:00", 12, 28500.0, 24000.0, 16, 14, "Lunch Peak 1"),
                HourlyTrendPoint("13:00", 13, 34800.0, 29500.0, 19, 17, "Lunch Peak 2"),
                HourlyTrendPoint("14:00", 14, 18900.0, 16200.0, 11, 10, "After Lunch"),
                HourlyTrendPoint("15:00", 15, 8200.0, 9100.0, 5, 6, "Afternoon Dip"),
                HourlyTrendPoint("16:00", 16, 9500.0, 8400.0, 6, 5, "Tea & Desserts"),
                HourlyTrendPoint("17:00", 17, 13800.0, 11800.0, 8, 7, "Evening Pickup"),
                HourlyTrendPoint("18:00", 18, 22400.0, 19200.0, 13, 11, "Dinner Start"),
                HourlyTrendPoint("19:00", 19, 36800.0, 31000.0, 21, 18, "Dinner Rush 1"),
                HourlyTrendPoint("20:00", 20, 44200.0, 36500.0, 24, 20, "Prime Dinner Peak"),
                HourlyTrendPoint("21:00", 21, 38100.0, 32400.0, 20, 17, "Late Dinner"),
                HourlyTrendPoint("22:00", 22, 19400.0, 17800.0, 11, 9, "Takeout / Dessert"),
                HourlyTrendPoint("23:00", 23, 7500.0, 6200.0, 4, 3, "Closing Night")
            )
            ComparisonPeriod.LAST_WEEK -> listOf(
                HourlyTrendPoint("08:00", 8, 3500.0, 3100.0, 3, 2, "Breakfast Rush"),
                HourlyTrendPoint("09:00", 9, 6200.0, 4800.0, 5, 4, "Morning Coffee"),
                HourlyTrendPoint("10:00", 10, 8400.0, 6900.0, 6, 5, "Bakery Pickups"),
                HourlyTrendPoint("11:00", 11, 14200.0, 12800.0, 9, 7, "Early Lunch"),
                HourlyTrendPoint("12:00", 12, 28500.0, 26200.0, 16, 15, "Lunch Peak 1"),
                HourlyTrendPoint("13:00", 13, 34800.0, 31800.0, 19, 18, "Lunch Peak 2"),
                HourlyTrendPoint("14:00", 14, 18900.0, 17500.0, 11, 11, "After Lunch"),
                HourlyTrendPoint("15:00", 15, 8200.0, 7900.0, 5, 5, "Afternoon Dip"),
                HourlyTrendPoint("16:00", 16, 9500.0, 8900.0, 6, 6, "Tea & Desserts"),
                HourlyTrendPoint("17:00", 17, 13800.0, 12400.0, 8, 7, "Evening Pickup"),
                HourlyTrendPoint("18:00", 18, 22400.0, 20800.0, 13, 12, "Dinner Start"),
                HourlyTrendPoint("19:00", 19, 36800.0, 33500.0, 21, 19, "Dinner Rush 1"),
                HourlyTrendPoint("20:00", 20, 44200.0, 39000.0, 24, 22, "Prime Dinner Peak"),
                HourlyTrendPoint("21:00", 21, 38100.0, 34200.0, 20, 18, "Late Dinner"),
                HourlyTrendPoint("22:00", 22, 19400.0, 16800.0, 11, 10, "Takeout / Dessert"),
                HourlyTrendPoint("23:00", 23, 7500.0, 5800.0, 4, 3, "Closing Night")
            )
            ComparisonPeriod.MONTH_AVG -> listOf(
                HourlyTrendPoint("08:00", 8, 3500.0, 2900.0, 3, 2, "Breakfast Rush"),
                HourlyTrendPoint("09:00", 9, 6200.0, 5400.0, 5, 4, "Morning Coffee"),
                HourlyTrendPoint("10:00", 10, 8400.0, 7200.0, 6, 5, "Bakery Pickups"),
                HourlyTrendPoint("11:00", 11, 14200.0, 12100.0, 9, 8, "Early Lunch"),
                HourlyTrendPoint("12:00", 12, 28500.0, 25000.0, 16, 14, "Lunch Peak 1"),
                HourlyTrendPoint("13:00", 13, 34800.0, 30200.0, 19, 17, "Lunch Peak 2"),
                HourlyTrendPoint("14:00", 14, 18900.0, 16800.0, 11, 10, "After Lunch"),
                HourlyTrendPoint("15:00", 15, 8200.0, 8300.0, 5, 5, "Afternoon Dip"),
                HourlyTrendPoint("16:00", 16, 9500.0, 8600.0, 6, 5, "Tea & Desserts"),
                HourlyTrendPoint("17:00", 17, 13800.0, 12000.0, 8, 7, "Evening Pickup"),
                HourlyTrendPoint("18:00", 18, 22400.0, 19800.0, 13, 12, "Dinner Start"),
                HourlyTrendPoint("19:00", 19, 36800.0, 32100.0, 21, 18, "Dinner Rush 1"),
                HourlyTrendPoint("20:00", 20, 44200.0, 37200.0, 24, 21, "Prime Dinner Peak"),
                HourlyTrendPoint("21:00", 21, 38100.0, 33000.0, 20, 18, "Late Dinner"),
                HourlyTrendPoint("22:00", 22, 19400.0, 17200.0, 11, 10, "Takeout / Dessert"),
                HourlyTrendPoint("23:00", 23, 7500.0, 6000.0, 4, 3, "Closing Night")
            )
        }
    }
}
