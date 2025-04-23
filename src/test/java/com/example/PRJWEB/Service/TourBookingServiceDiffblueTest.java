package com.example.PRJWEB.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.diffblue.cover.annotations.MethodsUnderTest;
import com.example.PRJWEB.Mapper.TourBookingMapper;
import com.example.PRJWEB.Repository.TourBookingRepository;
import com.example.PRJWEB.Repository.TourRepository;
import com.example.PRJWEB.Repository.UserRepository;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {TourBookingService.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class TourBookingServiceDiffblueTest {
    @MockitoBean
    private TourBookingMapper tourBookingMapper;

    @MockitoBean
    private TourBookingRepository tourBookingRepository;

    @Autowired
    private TourBookingService tourBookingService;

    @MockitoBean
    private TourRepository tourRepository;

    @MockitoBean
    private UserRepository userRepository;

    /**
     * Test {@link TourBookingService#calculateTotalPrice(BigDecimal, Integer, Integer)}.
     * <ul>
     *   <li>When minus one.</li>
     *   <li>Then throw {@link IllegalArgumentException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link TourBookingService#calculateTotalPrice(BigDecimal, Integer, Integer)}
     */
    @Test
    @DisplayName("Test calculateTotalPrice(BigDecimal, Integer, Integer); when minus one; then throw IllegalArgumentException")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"BigDecimal TourBookingService.calculateTotalPrice(BigDecimal, Integer, Integer)"})
    void testCalculateTotalPrice_whenMinusOne_thenThrowIllegalArgumentException() {
        // Arrange, Act and Assert
        assertThrows(IllegalArgumentException.class,
                () -> tourBookingService.calculateTotalPrice(new BigDecimal("2.3"), -1, -1));
    }

    /**
     * Test {@link TourBookingService#calculateTotalPrice(BigDecimal, Integer, Integer)}.
     * <ul>
     *   <li>When minus one.</li>
     *   <li>Then throw {@link IllegalArgumentException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link TourBookingService#calculateTotalPrice(BigDecimal, Integer, Integer)}
     */
    @Test
    @DisplayName("Test calculateTotalPrice(BigDecimal, Integer, Integer); when minus one; then throw IllegalArgumentException")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"BigDecimal TourBookingService.calculateTotalPrice(BigDecimal, Integer, Integer)"})
    void testCalculateTotalPrice_whenMinusOne_thenThrowIllegalArgumentException2() {
        // Arrange, Act and Assert
        assertThrows(IllegalArgumentException.class,
                () -> tourBookingService.calculateTotalPrice(new BigDecimal("2.3"), -1, 0));
    }

    /**
     * Test {@link TourBookingService#calculateTotalPrice(BigDecimal, Integer, Integer)}.
     * <ul>
     *   <li>When three.</li>
     *   <li>Then return {@link BigDecimal#BigDecimal(String)} with {@code 10.35}.</li>
     * </ul>
     * <p>
     * Method under test: {@link TourBookingService#calculateTotalPrice(BigDecimal, Integer, Integer)}
     */
    @Test
    @DisplayName("Test calculateTotalPrice(BigDecimal, Integer, Integer); when three; then return BigDecimal(String) with '10.35'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"BigDecimal TourBookingService.calculateTotalPrice(BigDecimal, Integer, Integer)"})
    void testCalculateTotalPrice_whenThree_thenReturnBigDecimalWith1035() {
        // Arrange and Act
        BigDecimal actualCalculateTotalPriceResult = tourBookingService.calculateTotalPrice(new BigDecimal("2.3"), 3, 3);

        // Assert
        assertEquals(new BigDecimal("10.35"), actualCalculateTotalPriceResult);
    }

    /**
     * Test {@link TourBookingService#calculateTotalPrice(BigDecimal, Integer, Integer)}.
     * <ul>
     *   <li>When zero.</li>
     *   <li>Then return {@link BigDecimal#BigDecimal(String)} with {@code 0.00}.</li>
     * </ul>
     * <p>
     * Method under test: {@link TourBookingService#calculateTotalPrice(BigDecimal, Integer, Integer)}
     */
    @Test
    @DisplayName("Test calculateTotalPrice(BigDecimal, Integer, Integer); when zero; then return BigDecimal(String) with '0.00'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"BigDecimal TourBookingService.calculateTotalPrice(BigDecimal, Integer, Integer)"})
    void testCalculateTotalPrice_whenZero_thenReturnBigDecimalWith000() {
        // Arrange and Act
        BigDecimal actualCalculateTotalPriceResult = tourBookingService.calculateTotalPrice(new BigDecimal("2.3"), 0, 0);

        // Assert
        assertEquals(new BigDecimal("0.00"), actualCalculateTotalPriceResult);
    }

    /**
     * Test {@link TourBookingService#calculateTotalPrice(BigDecimal, Integer, Integer)}.
     * <ul>
     *   <li>When zero.</li>
     *   <li>Then throw {@link IllegalArgumentException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link TourBookingService#calculateTotalPrice(BigDecimal, Integer, Integer)}
     */
    @Test
    @DisplayName("Test calculateTotalPrice(BigDecimal, Integer, Integer); when zero; then throw IllegalArgumentException")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"BigDecimal TourBookingService.calculateTotalPrice(BigDecimal, Integer, Integer)"})
    void testCalculateTotalPrice_whenZero_thenThrowIllegalArgumentException() {
        // Arrange, Act and Assert
        assertThrows(IllegalArgumentException.class,
                () -> tourBookingService.calculateTotalPrice(new BigDecimal("2.3"), 0, -1));
    }
}
