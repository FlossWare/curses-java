package org.flossware.curses.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link RenderingStyle} enum.
 *
 * <p>Validates all enum values, their contracts, and utility methods including:</p>
 * <ul>
 *   <li>Enum value existence and count</li>
 *   <li>valueOf() functionality and edge cases</li>
 *   <li>name() and toString() behavior</li>
 *   <li>Enum ordering and stability</li>
 * </ul>
 */
@DisplayName("RenderingStyle enum tests")
class RenderingStyleTest {

    /**
     * Tests that all expected enum values exist and are accessible.
     *
     * <p>Ensures the enum defines FLAT, RAISED, SUNKEN, and CUSTOM values,
     * which are the core rendering styles documented in the API.</p>
     */
    @Test
    @DisplayName("All enum values exist (FLAT, RAISED, SUNKEN, CUSTOM)")
    void testAllEnumValuesExist() {
        assertThat(RenderingStyle.FLAT)
            .isNotNull()
            .isInstanceOf(RenderingStyle.class);

        assertThat(RenderingStyle.RAISED)
            .isNotNull()
            .isInstanceOf(RenderingStyle.class);

        assertThat(RenderingStyle.SUNKEN)
            .isNotNull()
            .isInstanceOf(RenderingStyle.class);

        assertThat(RenderingStyle.CUSTOM)
            .isNotNull()
            .isInstanceOf(RenderingStyle.class);
    }

    /**
     * Tests that the enum contains exactly 4 values.
     *
     * <p>This test ensures no unexpected values are added or removed from the enum.
     * If this test fails, it indicates the enum contract has changed.</p>
     */
    @Test
    @DisplayName("Enum value count is exactly 4")
    void testEnumValueCount() {
        RenderingStyle[] values = RenderingStyle.values();

        assertThat(values)
            .hasSize(4)
            .containsExactly(
                RenderingStyle.FLAT,
                RenderingStyle.RAISED,
                RenderingStyle.SUNKEN,
                RenderingStyle.CUSTOM
            );
    }

    /**
     * Tests that each enum value has the correct name.
     *
     * <p>Validates that name() returns the expected constant name for each value.</p>
     */
    @ParameterizedTest
    @EnumSource(RenderingStyle.class)
    @DisplayName("Each enum value has correct name()")
    void testEnumValueNames(RenderingStyle style) {
        assertThat(style.name())
            .isNotNull()
            .isNotEmpty()
            .isIn("FLAT", "RAISED", "SUNKEN", "CUSTOM");
    }

    /**
     * Tests the specific name for each enum value.
     *
     * <p>Ensures each value returns its exact expected name.</p>
     */
    @Test
    @DisplayName("Specific enum values have correct names")
    void testSpecificEnumNames() {
        assertThat(RenderingStyle.FLAT.name()).isEqualTo("FLAT");
        assertThat(RenderingStyle.RAISED.name()).isEqualTo("RAISED");
        assertThat(RenderingStyle.SUNKEN.name()).isEqualTo("SUNKEN");
        assertThat(RenderingStyle.CUSTOM.name()).isEqualTo("CUSTOM");
    }

    /**
     * Tests that toString() returns the enum constant name.
     *
     * <p>By default, enum toString() returns the same value as name() unless overridden.
     * This test validates that behavior is preserved.</p>
     */
    @ParameterizedTest
    @EnumSource(RenderingStyle.class)
    @DisplayName("toString() returns correct value for each enum")
    void testToString(RenderingStyle style) {
        assertThat(style.toString())
            .isNotNull()
            .isNotEmpty()
            .isEqualTo(style.name());
    }

    /**
     * Tests specific toString() values for each enum constant.
     */
    @Test
    @DisplayName("Specific enum values have correct toString()")
    void testSpecificToString() {
        assertThat(RenderingStyle.FLAT.toString()).isEqualTo("FLAT");
        assertThat(RenderingStyle.RAISED.toString()).isEqualTo("RAISED");
        assertThat(RenderingStyle.SUNKEN.toString()).isEqualTo("SUNKEN");
        assertThat(RenderingStyle.CUSTOM.toString()).isEqualTo("CUSTOM");
    }

    /**
     * Tests that valueOf() correctly resolves valid enum constant names.
     *
     * <p>Validates successful parsing of all valid constant names.</p>
     */
    @ParameterizedTest
    @ValueSource(strings = {"FLAT", "RAISED", "SUNKEN", "CUSTOM"})
    @DisplayName("valueOf() works correctly for valid names")
    void testValueOfWithValidNames(String name) {
        RenderingStyle style = RenderingStyle.valueOf(name);

        assertThat(style)
            .isNotNull()
            .isInstanceOf(RenderingStyle.class);

        assertThat(style.name()).isEqualTo(name);
    }

    /**
     * Tests specific valueOf() resolution for each enum constant.
     */
    @Test
    @DisplayName("valueOf() resolves to correct enum instances")
    void testValueOfSpecificInstances() {
        assertThat(RenderingStyle.valueOf("FLAT")).isSameAs(RenderingStyle.FLAT);
        assertThat(RenderingStyle.valueOf("RAISED")).isSameAs(RenderingStyle.RAISED);
        assertThat(RenderingStyle.valueOf("SUNKEN")).isSameAs(RenderingStyle.SUNKEN);
        assertThat(RenderingStyle.valueOf("CUSTOM")).isSameAs(RenderingStyle.CUSTOM);
    }

    /**
     * Tests that valueOf() throws IllegalArgumentException for invalid names.
     *
     * <p>Validates that attempting to parse invalid enum constant names fails
     * with the expected exception type.</p>
     */
    @Test
    @DisplayName("valueOf() throws IllegalArgumentException for invalid names")
    void testValueOfWithInvalidName() {
        assertThatThrownBy(() -> RenderingStyle.valueOf("INVALID"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No enum constant");
    }

    /**
     * Tests that valueOf() is case-sensitive.
     *
     * <p>Enum constant names must match exactly, including case.</p>
     */
    @ParameterizedTest
    @ValueSource(strings = {"flat", "Flat", "raised", "Raised", "sunken", "Sunken", "custom", "Custom"})
    @DisplayName("valueOf() is case-sensitive")
    void testValueOfCaseSensitivity(String name) {
        assertThatThrownBy(() -> RenderingStyle.valueOf(name))
            .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests that valueOf() throws NullPointerException for null input.
     *
     * <p>This is the expected Java enum behavior for null arguments.</p>
     */
    @Test
    @DisplayName("valueOf() throws NullPointerException for null")
    void testValueOfWithNull() {
        assertThatThrownBy(() -> RenderingStyle.valueOf(null))
            .isInstanceOf(NullPointerException.class);
    }

    /**
     * Tests that valueOf() throws IllegalArgumentException for empty string.
     */
    @Test
    @DisplayName("valueOf() throws IllegalArgumentException for empty string")
    void testValueOfWithEmptyString() {
        assertThatThrownBy(() -> RenderingStyle.valueOf(""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests that valueOf() throws IllegalArgumentException for whitespace.
     */
    @Test
    @DisplayName("valueOf() throws IllegalArgumentException for whitespace")
    void testValueOfWithWhitespace() {
        assertThatThrownBy(() -> RenderingStyle.valueOf(" "))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> RenderingStyle.valueOf("  FLAT  "))
            .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests that enum values maintain consistent ordinal positions.
     *
     * <p>Validates the declaration order matches the expected sequence:
     * FLAT (0), RAISED (1), SUNKEN (2), CUSTOM (3).</p>
     *
     * <p>While ordinal values should not be relied upon for business logic,
     * they should remain stable for serialization compatibility.</p>
     */
    @Test
    @DisplayName("Enum ordinals are in correct order")
    void testEnumOrdinals() {
        assertThat(RenderingStyle.FLAT.ordinal()).isEqualTo(0);
        assertThat(RenderingStyle.RAISED.ordinal()).isEqualTo(1);
        assertThat(RenderingStyle.SUNKEN.ordinal()).isEqualTo(2);
        assertThat(RenderingStyle.CUSTOM.ordinal()).isEqualTo(3);
    }

    /**
     * Tests that values() returns a new array each time.
     *
     * <p>Modifying the returned array should not affect subsequent calls to values().</p>
     */
    @Test
    @DisplayName("values() returns a defensive copy")
    void testValuesReturnsDefensiveCopy() {
        RenderingStyle[] values1 = RenderingStyle.values();
        RenderingStyle[] values2 = RenderingStyle.values();

        assertThat(values1)
            .isNotSameAs(values2)
            .containsExactly(values2);

        // Modify first array
        values1[0] = null;

        // Second call should still return all values
        RenderingStyle[] values3 = RenderingStyle.values();
        assertThat(values3)
            .hasSize(4)
            .contains(RenderingStyle.FLAT);
    }

    /**
     * Tests enum equality using == operator.
     *
     * <p>Enum instances are singletons, so == comparison should work correctly.</p>
     */
    @Test
    @DisplayName("Enum equality works with == operator")
    void testEnumEquality() {
        RenderingStyle flat1 = RenderingStyle.FLAT;
        RenderingStyle flat2 = RenderingStyle.valueOf("FLAT");

        assertThat(flat1 == flat2).isTrue();
        assertThat(flat1).isSameAs(flat2);
    }

    /**
     * Tests enum inequality across different constants.
     */
    @Test
    @DisplayName("Different enum constants are not equal")
    void testEnumInequality() {
        assertThat(RenderingStyle.FLAT).isNotEqualTo(RenderingStyle.RAISED);
        assertThat(RenderingStyle.RAISED).isNotEqualTo(RenderingStyle.SUNKEN);
        assertThat(RenderingStyle.SUNKEN).isNotEqualTo(RenderingStyle.CUSTOM);
        assertThat(RenderingStyle.CUSTOM).isNotEqualTo(RenderingStyle.FLAT);
    }

    /**
     * Tests that enum can be used in switch statements.
     *
     * <p>Validates that all enum values can be matched in switch cases.</p>
     */
    @Test
    @DisplayName("Enum works correctly in switch statements")
    void testEnumInSwitch() {
        for (RenderingStyle style : RenderingStyle.values()) {
            String result = switch (style) {
                case FLAT -> "flat";
                case RAISED -> "raised";
                case SUNKEN -> "sunken";
                case CUSTOM -> "custom";
            };

            assertThat(result)
                .isNotNull()
                .isIn("flat", "raised", "sunken", "custom");
        }
    }

    /**
     * Tests that enum values are serializable.
     *
     * <p>All Java enums implement Serializable by default.</p>
     */
    @Test
    @DisplayName("Enum is serializable")
    void testEnumIsSerializable() {
        assertThat(RenderingStyle.FLAT)
            .isInstanceOf(java.io.Serializable.class);
    }

    /**
     * Tests that enum is comparable.
     *
     * <p>Enums are compared by their ordinal values.</p>
     */
    @Test
    @DisplayName("Enum is comparable by ordinal")
    void testEnumComparable() {
        assertThat(RenderingStyle.FLAT.compareTo(RenderingStyle.RAISED))
            .isNegative();

        assertThat(RenderingStyle.CUSTOM.compareTo(RenderingStyle.FLAT))
            .isPositive();

        assertThat(RenderingStyle.SUNKEN.compareTo(RenderingStyle.SUNKEN))
            .isZero();
    }

    /**
     * Tests enum class properties.
     *
     * <p>Validates that the enum class has expected metadata.</p>
     */
    @Test
    @DisplayName("Enum class has correct properties")
    void testEnumClassProperties() {
        Class<RenderingStyle> enumClass = RenderingStyle.class;

        assertThat(enumClass.isEnum()).isTrue();
        assertThat(enumClass.getEnumConstants())
            .hasSize(4)
            .containsExactly(
                RenderingStyle.FLAT,
                RenderingStyle.RAISED,
                RenderingStyle.SUNKEN,
                RenderingStyle.CUSTOM
            );
    }

    /**
     * Tests that all enum values are distinct.
     *
     * <p>Ensures no duplicate instances exist.</p>
     */
    @Test
    @DisplayName("All enum values are distinct")
    void testEnumValuesAreDistinct() {
        RenderingStyle[] values = RenderingStyle.values();

        assertThat(values)
            .doesNotHaveDuplicates()
            .hasSize(4);
    }
}
