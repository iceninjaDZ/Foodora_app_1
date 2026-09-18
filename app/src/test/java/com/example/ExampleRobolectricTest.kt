package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.StringsLocalization
import com.example.model.*
import com.example.ui.state.FoodoraViewModel
import com.example.ui.state.Screen
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Foodora Manager", appName)
    }

    @Test
    fun `test viewmodel initialization and sample data`() {
        val viewModel = FoodoraViewModel()
        assertNotNull(viewModel.businessProfile.value)
        assertTrue(viewModel.businessProfile.value.name.contains("Foodora Bistro & Grill"))
        assertTrue(viewModel.menuItems.value.isNotEmpty())
        assertTrue(viewModel.orders.value.isNotEmpty())
        assertTrue(viewModel.diningTables.value.isNotEmpty())
        assertTrue(viewModel.employees.value.isNotEmpty())
    }

    @Test
    fun `test pos cart and checkout flow`() {
        val viewModel = FoodoraViewModel()
        val item = viewModel.menuItems.value.first()
        viewModel.addToCart(item)
        assertEquals(1, viewModel.cart.value.size)
        assertEquals(1, viewModel.cart.value.first().quantity)

        viewModel.increaseCartItem(item)
        assertEquals(2, viewModel.cart.value.first().quantity)

        val order = viewModel.checkoutCart(PaymentMethod.CREDIT_CARD)
        assertNotNull(order)
        assertEquals(PaymentMethod.CREDIT_CARD, order!!.paymentMethod)
        assertEquals(0, viewModel.cart.value.size)
    }

    @Test
    fun `test kds order bump`() {
        val viewModel = FoodoraViewModel()
        val order = viewModel.orders.value.first { it.status == OrderStatus.PENDING }
        viewModel.advanceOrderStatus(order.id)
        val updated = viewModel.orders.value.first { it.id == order.id }
        assertEquals(OrderStatus.PREPARING, updated.status)
    }

    @Test
    fun `test table status update`() {
        val viewModel = FoodoraViewModel()
        val table = viewModel.diningTables.value.first()
        viewModel.updateTableStatus(table.id, TableStatus.OCCUPIED)
        val updated = viewModel.diningTables.value.first { it.id == table.id }
        assertEquals(TableStatus.OCCUPIED, updated.status)
    }

    @Test
    fun `test multi language dictionary`() {
        assertEquals("Kitchen (KDS)", StringsLocalization.get("nav_kds", Language.EN))
        assertEquals("شاشة المطبخ (KDS)", StringsLocalization.get("nav_kds", Language.AR))
        assertEquals("Cuisine (KDS)", StringsLocalization.get("nav_kds", Language.FR))
    }

    @Test
    fun `test role switching and screen navigation`() {
        val viewModel = FoodoraViewModel()
        viewModel.switchRole(UserRole.KITCHEN)
        assertEquals(UserRole.KITCHEN, viewModel.currentRole.value)
        assertEquals(Screen.KDS, viewModel.currentScreen.value)

        viewModel.switchRole(UserRole.CASHIER)
        assertEquals(Screen.POS, viewModel.currentScreen.value)

        viewModel.navigateTo(Screen.QR_MENU)
        assertEquals(Screen.QR_MENU, viewModel.currentScreen.value)

        viewModel.navigateTo(Screen.ANALYTICS)
        assertEquals(Screen.ANALYTICS, viewModel.currentScreen.value)
    }

    @Test
    fun `test daily analytics and recharts period comparison trends`() {
        val viewModel = FoodoraViewModel()
        assertEquals(ComparisonPeriod.YESTERDAY, viewModel.selectedComparisonPeriod.value)
        val initialTrends = viewModel.hourlySalesTrends.value
        assertTrue(initialTrends.isNotEmpty())
        assertEquals(16, initialTrends.size)

        // Verify peak hour is calculated properly
        val peak = initialTrends.maxByOrNull { it.todayRevenue }
        assertNotNull(peak)
        assertEquals(20, peak!!.hour24)
        assertEquals(44200.0, peak.todayRevenue, 0.01)

        // Switch period to LAST_WEEK
        viewModel.selectComparisonPeriod(ComparisonPeriod.LAST_WEEK)
        assertEquals(ComparisonPeriod.LAST_WEEK, viewModel.selectedComparisonPeriod.value)
        val lastWeekTrends = viewModel.hourlySalesTrends.value
        assertEquals(16, lastWeekTrends.size)
        assertEquals(39000.0, lastWeekTrends.first { it.hour24 == 20 }.previousPeriodRevenue, 0.01)

        // Switch period to MONTH_AVG
        viewModel.selectComparisonPeriod(ComparisonPeriod.MONTH_AVG)
        assertEquals(ComparisonPeriod.MONTH_AVG, viewModel.selectedComparisonPeriod.value)
        val monthAvgTrends = viewModel.hourlySalesTrends.value
        assertEquals(37200.0, monthAvgTrends.first { it.hour24 == 20 }.previousPeriodRevenue, 0.01)
    }
}
