/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.operator.scalar;

import com.facebook.presto.spi.PrestoException;
import com.facebook.presto.spi.type.SqlDecimal;
import org.testng.annotations.Test;

import static com.facebook.presto.SessionTestUtils.TEST_SESSION;
import static com.facebook.presto.spi.StandardErrorCode.NUMERIC_VALUE_OUT_OF_RANGE;
import static com.facebook.presto.spi.type.BigintType.BIGINT;
import static com.facebook.presto.spi.type.BooleanType.BOOLEAN;
import static com.facebook.presto.spi.type.DecimalType.createDecimalType;
import static com.facebook.presto.spi.type.DoubleType.DOUBLE;
import static com.facebook.presto.spi.type.VarcharType.VARCHAR;

public class TestMathFunctions
        extends AbstractTestFunctions
{
    private static final double[] DOUBLE_VALUES = {123, -123, 123.45, -123.45};
    private static final long[] longLefts = {9, 10, 11, -9, -10, -11};
    private static final long[] longRights = {3, -3};
    private static final double[] doubleLefts = {9, 10, 11, -9, -10, -11, 9.1, 10.1, 11.1, -9.1, -10.1, -11.1};
    private static final double[] doubleRights = {3, -3, 3.1, -3.1};

    @Test
    public void testAbs()
    {
        assertFunction("abs(123)", BIGINT, 123L);
        assertFunction("abs(-123)", BIGINT, 123L);
        assertFunction("abs(CAST(NULL AS BIGINT))", BIGINT, null);
        assertFunction("abs(DOUBLE '123.0')", DOUBLE, 123.0);
        assertFunction("abs(DOUBLE '-123.0')", DOUBLE, 123.0);
        assertFunction("abs(DOUBLE '123.45')", DOUBLE, 123.45);
        assertFunction("abs(DOUBLE '-123.45')", DOUBLE, 123.45);
        assertFunction("abs(CAST(NULL AS DOUBLE))", DOUBLE, null);
        assertInvalidFunction("abs(-9223372036854775807 - if(rand() < 10, 1, 1))", NUMERIC_VALUE_OUT_OF_RANGE);
    }

    @Test
    public void testAcos()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("acos(" + doubleValue + ")", DOUBLE, Math.acos(doubleValue));
        }
        assertFunction("acos(NULL)", DOUBLE, null);
    }

    @Test
    public void testAsin()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("asin(" + doubleValue + ")", DOUBLE, Math.asin(doubleValue));
        }
        assertFunction("asin(NULL)", DOUBLE, null);
    }

    @Test
    public void testAtan()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("atan(" + doubleValue + ")", DOUBLE, Math.atan(doubleValue));
        }
        assertFunction("atan(NULL)", DOUBLE, null);
    }

    @Test
    public void testAtan2()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("atan2(" + doubleValue + ", " + doubleValue + ")", DOUBLE, Math.atan2(doubleValue, doubleValue));
        }
        assertFunction("atan2(NULL, NULL)", DOUBLE, null);
        assertFunction("atan2(1.0, NULL)", DOUBLE, null);
        assertFunction("atan2(NULL, 1.0)", DOUBLE, null);
    }

    @Test
    public void testCbrt()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("cbrt(" + doubleValue + ")", DOUBLE, Math.cbrt(doubleValue));
        }
        assertFunction("cbrt(NULL)", DOUBLE, null);
    }

    @Test
    public void testCeil()
    {
        assertFunction("ceil(123)", BIGINT, 123);
        assertFunction("ceil(-123)", BIGINT, -123);
        assertFunction("ceil(CAST(NULL as BIGINT))", BIGINT, null);
        assertFunction("ceil(DOUBLE '123.0')", DOUBLE, 123.0);
        assertFunction("ceil(DOUBLE '-123.0')", DOUBLE, -123.0);
        assertFunction("ceil(DOUBLE '123.45')", DOUBLE, 124.0);
        assertFunction("ceil(DOUBLE '-123.45')", DOUBLE, -123.0);
        assertFunction("ceil(CAST(NULL as DOUBLE))", DOUBLE, null);
        assertFunction("ceil(DECIMAL '123.45')", createDecimalType(4), SqlDecimal.of("124"));
        assertFunction("ceil(DECIMAL '1234567890123456.78')", createDecimalType(17), SqlDecimal.of("1234567890123457"));
        assertFunction("ceiling(123)", BIGINT, 123);
        assertFunction("ceiling(-123)", BIGINT, -123);
        assertFunction("ceiling(CAST(NULL AS BIGINT))", BIGINT, null);
        assertFunction("ceiling(DOUBLE '123.0')", DOUBLE, 123.0);
        assertFunction("ceiling(DOUBLE '-123.0')", DOUBLE, -123.0);
        assertFunction("ceiling(DOUBLE '123.45')", DOUBLE, 124.0);
        assertFunction("ceiling(DOUBLE '-123.45')", DOUBLE, -123.0);
        assertFunction("ceiling(CAST(NULL AS DOUBLE))", DOUBLE, null);
        assertFunction("ceiling(DECIMAL '123')", createDecimalType(3), SqlDecimal.of("123"));
        assertFunction("ceiling(DECIMAL '-123')", createDecimalType(3), SqlDecimal.of("-123"));
        assertFunction("ceiling(DECIMAL '123.45')", createDecimalType(4), SqlDecimal.of("124"));
        assertFunction("ceiling(DECIMAL '-123.45')", createDecimalType(4), SqlDecimal.of("-123"));
        assertFunction("ceiling(DECIMAL '999.9')", createDecimalType(4), SqlDecimal.of("1000"));
        assertFunction("ceiling(DECIMAL '123456789012345678')", createDecimalType(18), SqlDecimal.of("123456789012345678"));
        assertFunction("ceiling(DECIMAL '-123456789012345678')", createDecimalType(18), SqlDecimal.of("-123456789012345678"));
        assertFunction("ceiling(DECIMAL '123456789012345678.00')", createDecimalType(19), SqlDecimal.of("123456789012345678"));
        assertFunction("ceiling(DECIMAL '123456789012345678.99')", createDecimalType(19), SqlDecimal.of("123456789012345679"));
        assertFunction("ceiling(DECIMAL '-123456789012345678.99')", createDecimalType(19), SqlDecimal.of("-123456789012345678"));
        assertFunction("ceiling(DECIMAL '999999999999999999.9')", createDecimalType(19), SqlDecimal.of("1000000000000000000"));
        assertFunction("ceiling(DECIMAL '1234567890123456.78')", createDecimalType(17), SqlDecimal.of("1234567890123457"));
        assertFunction("ceiling(DECIMAL '-1234567890123456.78')", createDecimalType(17), SqlDecimal.of("-1234567890123456"));
        assertFunction("ceiling(CAST(NULL AS DECIMAL(1,0)))", createDecimalType(1), null);
    }

    @Test
    public void testTruncate()
    {
        final String maxDouble = Double.toString(Double.MAX_VALUE);
        final String minDouble = Double.toString(-Double.MAX_VALUE);
        assertFunction("truncate(17.18)", DOUBLE, 17.0);
        assertFunction("truncate(-17.18)", DOUBLE, -17.0);
        assertFunction("truncate(17.88)", DOUBLE, 17.0);
        assertFunction("truncate(-17.88)", DOUBLE, -17.0);
        assertFunction("truncate(NULL)", DOUBLE, null);
        assertFunction("truncate(CAST(NULL AS DOUBLE))", DOUBLE, null);
        assertFunction("truncate(" + maxDouble + ")", DOUBLE, Double.MAX_VALUE);
        assertFunction("truncate(" + minDouble + ")", DOUBLE, -Double.MAX_VALUE);
    }

    @Test
    public void testCos()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("cos(" + doubleValue + ")", DOUBLE, Math.cos(doubleValue));
        }
        assertFunction("cos(NULL)", DOUBLE, null);
    }

    @Test
    public void testCosh()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("cosh(" + doubleValue + ")", DOUBLE, Math.cosh(doubleValue));
        }
        assertFunction("cosh(NULL)", DOUBLE, null);
    }

    @Test
    public void testDegrees()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction(String.format("degrees(%s)", doubleValue), DOUBLE, Math.toDegrees(doubleValue));
        }
        assertFunction("degrees(NULL)", DOUBLE, null);
    }

    @Test
    public void testE()
    {
        assertFunction("e()", DOUBLE, Math.E);
    }

    @Test
    public void testExp()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("exp(" + doubleValue + ")", DOUBLE, Math.exp(doubleValue));
        }
        assertFunction("exp(NULL)", DOUBLE, null);
    }

    @Test
    public void testFloor()
    {
        assertFunction("floor(123)", BIGINT, 123);
        assertFunction("floor(-123)", BIGINT, -123);
        assertFunction("floor(CAST(NULL as BIGINT))", BIGINT, null);
        assertFunction("floor(DOUBLE '123.0')", DOUBLE, 123.0);
        assertFunction("floor(DOUBLE '-123.0')", DOUBLE, -123.0);
        assertFunction("floor(DOUBLE '123.45')", DOUBLE, 123.0);
        assertFunction("floor(DOUBLE '-123.45')", DOUBLE, -124.0);
        assertFunction("floor(CAST(NULL as DOUBLE))", DOUBLE, null);
        assertFunction("floor(DECIMAL '123')", createDecimalType(3), SqlDecimal.of("123"));
        assertFunction("floor(DECIMAL '-123')", createDecimalType(3), SqlDecimal.of("-123"));
        assertFunction("floor(DECIMAL '123.45')", createDecimalType(4), SqlDecimal.of("123"));
        assertFunction("floor(DECIMAL '-123.45')", createDecimalType(4), SqlDecimal.of("-124"));
        assertFunction("floor(DECIMAL '-999.9')", createDecimalType(4), SqlDecimal.of("-1000"));
        assertFunction("floor(DECIMAL '123456789012345678')", createDecimalType(18), SqlDecimal.of("123456789012345678"));
        assertFunction("floor(DECIMAL '-123456789012345678')", createDecimalType(18), SqlDecimal.of("-123456789012345678"));
        assertFunction("floor(DECIMAL '123456789012345678.00')", createDecimalType(19), SqlDecimal.of("123456789012345678"));
        assertFunction("floor(DECIMAL '123456789012345678.99')", createDecimalType(19), SqlDecimal.of("123456789012345678"));
        assertFunction("floor(DECIMAL '-123456789012345678.99')", createDecimalType(19), SqlDecimal.of("-123456789012345679"));
        assertFunction("floor(DECIMAL '-999999999999999999.9')", createDecimalType(19), SqlDecimal.of("-1000000000000000000"));
        assertFunction("floor(DECIMAL '1234567890123456.78')", createDecimalType(17), SqlDecimal.of("1234567890123456"));
        assertFunction("floor(DECIMAL '-1234567890123456.78')", createDecimalType(17), SqlDecimal.of("-1234567890123457"));
        assertFunction("floor(CAST(NULL as DECIMAL(1,0)))", createDecimalType(1), null);
    }

    @Test
    public void testLn()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("ln(" + doubleValue + ")", DOUBLE, Math.log(doubleValue));
        }
        assertFunction("ln(NULL)", DOUBLE, null);
    }

    @Test
    public void testLog2()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("log2(" + doubleValue + ")", DOUBLE, Math.log(doubleValue) / Math.log(2));
        }
        assertFunction("log2(NULL)", DOUBLE, null);
    }

    @Test
    public void testLog10()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("log10(" + doubleValue + ")", DOUBLE, Math.log10(doubleValue));
        }
        assertFunction("log10(NULL)", DOUBLE, null);
    }

    @Test
    public void testLog()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            for (double base : DOUBLE_VALUES) {
                assertFunction("log(" + doubleValue + ", " + base + ")", DOUBLE, Math.log(doubleValue) / Math.log(base));
            }
        }
        assertFunction("log(NULL, NULL)", DOUBLE, null);
        assertFunction("log(5.0, NULL)", DOUBLE, null);
        assertFunction("log(NULL, 5.0)", DOUBLE, null);
    }

    @Test
    public void testMod()
    {
        for (long left : longLefts) {
            for (long right : longRights) {
                assertFunction("mod(" + left + ", " + right + ")", BIGINT, left % right);
            }
        }

        for (long left : longLefts) {
            for (double right : doubleRights) {
                assertFunction("mod(" + left + ", DOUBLE '" + right + "')", DOUBLE, left % right);
            }
        }

        for (double left : doubleLefts) {
            for (long right : longRights) {
                assertFunction("mod(DOUBLE '" + left + "', " + right + ")", DOUBLE, left % right);
            }
        }

        for (double left : doubleLefts) {
            for (double right : doubleRights) {
                assertFunction("mod(DOUBLE '" + left + "', DOUBLE '" + right + "')", DOUBLE, left % right);
            }
        }
        assertFunction("mod(DOUBLE '5.0', NULL)", DOUBLE, null);
        assertFunction("mod(NULL, DOUBLE '5.0')", DOUBLE, null);
    }

    @Test
    public void testPi()
    {
        assertFunction("pi()", DOUBLE, Math.PI);
    }

    @Test
    public void testNaN()
    {
        assertFunction("nan()", DOUBLE, Double.NaN);
        assertFunction("CAST(0.0 as DOUBLE) / CAST(0.0 as DOUBLE)", DOUBLE, Double.NaN);
    }

    @Test
    public void testInfinity()
    {
        assertFunction("infinity()", DOUBLE, Double.POSITIVE_INFINITY);
        assertFunction("-rand() / 0.0", DOUBLE, Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testIsInfinite()
    {
        assertFunction("is_infinite(CAST(1.0 as DOUBLE) / CAST(0.0 as DOUBLE))", BOOLEAN, true);
        assertFunction("is_infinite(CAST(0.0 as DOUBLE) / CAST(0.0 as DOUBLE))", BOOLEAN, false);
        assertFunction("is_infinite(CAST(1.0 as DOUBLE) / CAST(1.0 as DOUBLE))", BOOLEAN, false);
        assertFunction("is_infinite(NULL)", BOOLEAN, null);
    }

    @Test
    public void testIsFinite()
    {
        assertFunction("is_finite(100000)", BOOLEAN, true);
        assertFunction("is_finite(rand() / 0.0)", BOOLEAN, false);
        assertFunction("is_finite(NULL)", BOOLEAN, null);
    }

    @Test
    public void testIsNaN()
    {
        assertFunction("is_nan(CAST(0.0 as DOUBLE) / CAST(0.0 as DOUBLE))", BOOLEAN, true);
        assertFunction("is_nan(CAST(0.0 as DOUBLE) / CAST(1.0 as DOUBLE))", BOOLEAN, false);
        assertFunction("is_nan(infinity() / infinity())", BOOLEAN, true);
        assertFunction("is_nan(nan())", BOOLEAN, true);
        assertFunction("is_nan(NULL)", BOOLEAN, null);
    }

    @Test
    public void testPower()
    {
        for (long left : longLefts) {
            for (long right : longRights) {
                assertFunction("power(" + left + ", " + right + ")", DOUBLE, Math.pow(left, right));
            }
        }

        for (long left : longLefts) {
            for (double right : doubleRights) {
                assertFunction("power(" + left + ", " + right + ")", DOUBLE, Math.pow(left, right));
            }
        }

        for (double left : doubleLefts) {
            for (long right : longRights) {
                assertFunction("power(" + left + ", " + right + ")", DOUBLE, Math.pow(left, right));
            }
        }

        for (double left : doubleLefts) {
            for (double right : doubleRights) {
                assertFunction("power(" + left + ", " + right + ")", DOUBLE, Math.pow(left, right));
            }
        }

        assertFunction("power(NULL, NULL)", DOUBLE, null);
        assertFunction("power(5.0, NULL)", DOUBLE, null);
        assertFunction("power(NULL, 5.0)", DOUBLE, null);

        // test alias
        assertFunction("pow(5.0, 2.0)", DOUBLE, 25.0);
    }

    @Test
    public void testRadians()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction(String.format("radians(%s)", doubleValue), DOUBLE, Math.toRadians(doubleValue));
        }
        assertFunction("radians(NULL)", DOUBLE, null);
    }

    @Test
    public void testRandom()
    {
        // random is non-deterministic
        functionAssertions.tryEvaluateWithAll("rand()", DOUBLE, TEST_SESSION);
        functionAssertions.tryEvaluateWithAll("random()", DOUBLE, TEST_SESSION);
        functionAssertions.tryEvaluateWithAll("rand(1000)", BIGINT, TEST_SESSION);
        functionAssertions.tryEvaluateWithAll("random(2000)", BIGINT, TEST_SESSION);

        assertInvalidFunction("rand(-1)", "bound must be positive");
    }

    @Test
    public void testRound()
    {
        assertFunction("round( 3)", BIGINT, 3);
        assertFunction("round(-3)", BIGINT, -3);
        assertFunction("round(CAST(NULL as BIGINT))", BIGINT, null);
        assertFunction("round(DOUBLE '3.0')", DOUBLE, 3.0);
        assertFunction("round(DOUBLE '-3.0')", DOUBLE, -3.0);
        assertFunction("round(DOUBLE '3.499')", DOUBLE, 3.0);
        assertFunction("round(DOUBLE '-3.499')", DOUBLE, -3.0);
        assertFunction("round(DOUBLE '3.5')", DOUBLE, 4.0);
        assertFunction("round(DOUBLE '-3.5')", DOUBLE, -4.0);
        assertFunction("round(DOUBLE '-3.5001')", DOUBLE, -4.0);
        assertFunction("round(DOUBLE '-3.99')", DOUBLE, -4.0);
        assertFunction("round(CAST(NULL as DOUBLE))", DOUBLE, null);

        assertFunction("round( 3, 0)", BIGINT, 3);
        assertFunction("round(-3, 0)", BIGINT, -3);
        assertFunction("round(DOUBLE '3.0', 0)", DOUBLE, 3.0);
        assertFunction("round(DOUBLE '-3.0', 0)", DOUBLE, -3.0);
        assertFunction("round(DOUBLE '3.499', 0)", DOUBLE, 3.0);
        assertFunction("round(DOUBLE '-3.499', 0)", DOUBLE, -3.0);
        assertFunction("round(DOUBLE '3.5', 0)", DOUBLE, 4.0);
        assertFunction("round(DOUBLE '-3.5', 0)", DOUBLE, -4.0);
        assertFunction("round(DOUBLE '-3.5001', 0)", DOUBLE, -4.0);
        assertFunction("round(DOUBLE '-3.99', 0)", DOUBLE, -4.0);

        assertFunction("round( 3, 1)", BIGINT, 3);
        assertFunction("round(-3, 1)", BIGINT, -3);
        assertFunction("round(CAST(NULL as BIGINT), CAST(NULL as BIGINT))", BIGINT, null);
        assertFunction("round(-3, CAST(NULL as BIGINT))", BIGINT, null);
        assertFunction("round(CAST(NULL as BIGINT), 1)", BIGINT, null);
        assertFunction("round(DOUBLE '3.0', 1)", DOUBLE, 3.0);
        assertFunction("round(DOUBLE '-3.0', 1)", DOUBLE, -3.0);
        assertFunction("round(DOUBLE '3.499', 1)", DOUBLE, 3.5);
        assertFunction("round(DOUBLE '-3.499', 1)", DOUBLE, -3.5);
        assertFunction("round(DOUBLE '3.5', 1)", DOUBLE, 3.5);
        assertFunction("round(DOUBLE '-3.5', 1)", DOUBLE, -3.5);
        assertFunction("round(DOUBLE '-3.5001', 1)", DOUBLE, -3.5);
        assertFunction("round(DOUBLE '-3.99', 1)", DOUBLE, -4.0);
        assertFunction("round(CAST(NULL as DOUBLE), CAST(NULL as BIGINT))", DOUBLE, null);
        assertFunction("round(DOUBLE '-3.0', CAST(NULL as BIGINT))", DOUBLE, null);
        assertFunction("round(CAST(NULL as DOUBLE), 1)", DOUBLE, null);
    }

    @Test
    public void testSin()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("sin(" + doubleValue + ")", DOUBLE, Math.sin(doubleValue));
        }
        assertFunction("sin(NULL)", DOUBLE, null);
    }

    @Test
    public void testSqrt()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("sqrt(" + doubleValue + ")", DOUBLE, Math.sqrt(doubleValue));
        }
        assertFunction("sqrt(NULL)", DOUBLE, null);
    }

    @Test
    public void testTan()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("tan(" + doubleValue + ")", DOUBLE, Math.tan(doubleValue));
        }
        assertFunction("tan(NULL)", DOUBLE, null);
    }

    @Test
    public void testTanh()
    {
        for (double doubleValue : DOUBLE_VALUES) {
            assertFunction("tanh(" + doubleValue + ")", DOUBLE, Math.tanh(doubleValue));
        }
        assertFunction("tanh(NULL)", DOUBLE, null);
    }

    @Test
    public void testGreatest()
            throws Exception
    {
        // bigint
        assertFunction("greatest(1, 2)", BIGINT, 2);
        assertFunction("greatest(-1, -2)", BIGINT, -1);
        assertFunction("greatest(5, 4, 3, 2, 1, 2, 3, 4, 1, 5)", BIGINT, 5);
        assertFunction("greatest(-1)", BIGINT, -1);
        assertFunction("greatest(5, 4, CAST(NULL as BIGINT), 3)", BIGINT, null);

        // double
        assertFunction("greatest(CAST(CAST(1.5 as DOUBLE) as DOUBLE), CAST(2.3 as DOUBLE))", DOUBLE, 2.3);
        assertFunction("greatest(-CAST(1.5 as DOUBLE), -CAST(2.3 as DOUBLE))", DOUBLE, -1.5);
        assertFunction("greatest(-CAST(1.5 as DOUBLE), -CAST(2.3 as DOUBLE), -5/3)", DOUBLE, -1.0);
        assertFunction("greatest(CAST(1.5 as DOUBLE), -CAST(1.0 as DOUBLE) / CAST(0.0 as DOUBLE), CAST(1.0 as DOUBLE) / CAST(0.0 as DOUBLE))", DOUBLE, Double.POSITIVE_INFINITY);
        assertFunction("greatest(5, 4, CAST(NULL as DOUBLE), 3)", DOUBLE, null);

        // decimal
        assertDecimalFunction("greatest(1.0, 2.0)", decimal("2.0"));
        assertDecimalFunction("greatest(1.0, -2.0)", decimal("1.0"));
        assertDecimalFunction("greatest(1.0, 1.1, 1.2, 1.3)", decimal("1.3"));

        // mixed
        assertFunction("greatest(1, CAST(2.0 as DOUBLE))", DOUBLE, 2.0);
        assertFunction("greatest(CAST(1.0 as DOUBLE), 2)", DOUBLE, 2.0);
        assertFunction("greatest(CAST(5.0 as DOUBLE), 4, CAST(NULL as DOUBLE), 3)", DOUBLE, null);
        assertFunction("greatest(CAST(5.0 as DOUBLE), 4, CAST(NULL as BIGINT), 3)", DOUBLE, null);
        assertFunction("greatest(1.0, CAST(2.0 as DOUBLE))", DOUBLE, 2.0);
        assertDecimalFunction("greatest(5, 4, 3.0, 2)", decimal("0000000000000000005.0"));

        // invalid
        assertInvalidFunction("greatest(CAST(1.5 as DOUBLE), CAST(0.0 as DOUBLE) / CAST(0.0 as DOUBLE))", "Invalid argument to greatest(): NaN");
    }

    @Test
    public void testLeast()
            throws Exception
    {
        // bigint
        assertFunction("least(1, 2)", BIGINT, 1);
        assertFunction("least(-1, -2)", BIGINT, -2);
        assertFunction("least(5, 4, 3, 2, 1, 2, 3, 4, 1, 5)", BIGINT, 1);
        assertFunction("least(-1)", BIGINT, -1);
        assertFunction("least(5, 4, CAST(NULL as BIGINT), 3)", BIGINT, null);

        // double
        assertFunction("least(CAST(1.5 as DOUBLE), CAST(2.3 as DOUBLE))", DOUBLE, 1.5);
        assertFunction("least(-CAST(1.5 as DOUBLE), -CAST(2.3 as DOUBLE))", DOUBLE, -2.3);
        assertFunction("least(-CAST(1.5 as DOUBLE), -CAST(2.3 as DOUBLE), -5/3)", DOUBLE, -2.3);
        assertFunction("least(CAST(1.5 as DOUBLE), -CAST(1.0 as DOUBLE) / CAST(0.0 as DOUBLE), CAST(1.0 as DOUBLE) / CAST(0.0 as DOUBLE))", DOUBLE, Double.NEGATIVE_INFINITY);
        assertFunction("least(5, 4, CAST(NULL as DOUBLE), 3)", DOUBLE, null);

        // decimal
        assertDecimalFunction("least(1.0, 2.0)", decimal("1.0"));
        assertDecimalFunction("least(1.0, -2.0)", decimal("-2.0"));
        assertDecimalFunction("least(1.0, 1.1, 1.2, 1.3)", decimal("1.0"));

        // mixed
        assertFunction("least(1, CAST(2.0 as DOUBLE))", DOUBLE, 1.0);
        assertFunction("least(CAST(1.0 as DOUBLE), 2)", DOUBLE, 1.0);
        assertFunction("least(CAST(5.0 as DOUBLE), 4, CAST(NULL as DOUBLE), 3)", DOUBLE, null);
        assertFunction("least(CAST(5.0 as DOUBLE), 4, CAST(NULL as BIGINT), 3)", DOUBLE, null);
        assertFunction("least(1.0, CAST(2.0 as DOUBLE))", DOUBLE, 1.0);
        assertDecimalFunction("least(5, 4, 3.0, 2)", decimal("0000000000000000002.0"));

        // invalid
        assertInvalidFunction("least(CAST(1.5 as DOUBLE), CAST(0.0 as DOUBLE) / CAST(0.0 as DOUBLE))", "Invalid argument to least(): NaN");
    }

    @Test(expectedExceptions = PrestoException.class, expectedExceptionsMessageRegExp = "\\QInvalid argument to greatest(): NaN\\E")
    public void testGreatestWithNaN()
            throws Exception
    {
        functionAssertions.tryEvaluate("greatest(CAST(1.5 as DOUBLE), CAST(0.0 as DOUBLE) / CAST(0.0 as DOUBLE))", DOUBLE);
    }

    @Test
    public void testToBase()
            throws Exception
    {
        assertFunction("to_base(2147483648, 16)", VARCHAR, "80000000");
        assertFunction("to_base(255, 2)", VARCHAR, "11111111");
        assertFunction("to_base(-2147483647, 16)", VARCHAR, "-7fffffff");
        assertFunction("to_base(NULL, 16)", VARCHAR, null);
        assertFunction("to_base(-2147483647, NULL)", VARCHAR, null);
        assertFunction("to_base(NULL, NULL)", VARCHAR, null);
        assertInvalidFunction("to_base(255, 1)", VARCHAR, "Radix must be between 2 and 36");
        assertInvalidFunction("to_base(255, 1)", "Radix must be between 2 and 36");
    }

    @Test
    public void testFromBase()
            throws Exception
    {
        assertFunction("from_base('80000000', 16)", BIGINT, 2147483648L);
        assertFunction("from_base('11111111', 2)", BIGINT, 255);
        assertFunction("from_base('-7fffffff', 16)", BIGINT, -2147483647);
        assertFunction("from_base('9223372036854775807', 10)", BIGINT, 9223372036854775807L);
        assertFunction("from_base('-9223372036854775808', 10)", BIGINT, -9223372036854775808L);
        assertFunction("from_base(NULL, 10)", BIGINT, null);
        assertFunction("from_base('-9223372036854775808', NULL)", BIGINT, null);
        assertFunction("from_base(NULL, NULL)", BIGINT, null);
        assertInvalidFunction("from_base('Z', 37)", "Radix must be between 2 and 36");
        assertInvalidFunction("from_base('Z', 35)", "Not a valid base-35 number: Z");
        assertInvalidFunction("from_base('9223372036854775808', 10)", "Not a valid base-10 number: 9223372036854775808");
        assertInvalidFunction("from_base('Z', 37)", BIGINT, "Radix must be between 2 and 36");
        assertInvalidFunction("from_base('Z', 35)", BIGINT, "Not a valid base-35 number: Z");
        assertInvalidFunction("from_base('9223372036854775808', 10)", BIGINT, "Not a valid base-10 number: 9223372036854775808");
    }

    @Test
    public void testWidthBucket()
            throws Exception
    {
        assertFunction("width_bucket(3.14, 0, 4, 3)", BIGINT, 3);
        assertFunction("width_bucket(2, 0, 4, 3)", BIGINT, 2);
        assertFunction("width_bucket(infinity(), 0, 4, 3)", BIGINT, 4);
        assertFunction("width_bucket(-1, 0, 3.2, 4)", BIGINT, 0);

        // bound1 > bound2 is not symmetric with bound2 > bound1
        assertFunction("width_bucket(3.14, 4, 0, 3)", BIGINT, 1);
        assertFunction("width_bucket(2, 4, 0, 3)", BIGINT, 2);
        assertFunction("width_bucket(infinity(), 4, 0, 3)", BIGINT, 0);
        assertFunction("width_bucket(-1, 3.2, 0, 4)", BIGINT, 5);

        // failure modes
        assertInvalidFunction("width_bucket(3.14, 0, 4, 0)", "bucketCount must be greater than 0");
        assertInvalidFunction("width_bucket(3.14, 0, 4, -1)", "bucketCount must be greater than 0");
        assertInvalidFunction("width_bucket(nan(), 0, 4, 3)", "operand must not be NaN");
        assertInvalidFunction("width_bucket(3.14, -1, -1, 3)", "bounds cannot equal each other");
        assertInvalidFunction("width_bucket(3.14, nan(), -1, 3)", "first bound must be finite");
        assertInvalidFunction("width_bucket(3.14, -1, nan(), 3)", "second bound must be finite");
        assertInvalidFunction("width_bucket(3.14, infinity(), -1, 3)", "first bound must be finite");
        assertInvalidFunction("width_bucket(3.14, -1, infinity(), 3)", "second bound must be finite");
    }

    @Test(expectedExceptions = PrestoException.class, expectedExceptionsMessageRegExp = "Bucket for value Infinity is out of range")
    public void testWidthBucketOverflowAscending()
            throws Exception
    {
        functionAssertions.tryEvaluate("width_bucket(infinity(), 0, 4, " + Long.MAX_VALUE + ")", DOUBLE);
    }

    @Test(expectedExceptions = PrestoException.class, expectedExceptionsMessageRegExp = "Bucket for value Infinity is out of range")
    public void testWidthBucketOverflowDescending()
            throws Exception
    {
        functionAssertions.tryEvaluate("width_bucket(infinity(), 4, 0, " + Long.MAX_VALUE + ")", DOUBLE);
    }

    @Test
    public void testWidthBucketArray()
            throws Exception
    {
        assertFunction("width_bucket(3.14, array[0.0, 2.0, 4.0])", BIGINT, 2);
        assertFunction("width_bucket(infinity(), array[0.0, 2.0, 4.0])", BIGINT, 3);
        assertFunction("width_bucket(-1, array[0.0, 1.2, 3.3, 4.5])", BIGINT, 0);

        // edge case of only a single bin
        assertFunction("width_bucket(3.145, array[0.0])", BIGINT, 1);
        assertFunction("width_bucket(-3.145, array[0.0])", BIGINT, 0);

        // failure modes
        assertInvalidFunction("width_bucket(3.14, array[])", "Bins cannot be an empty array");
        assertInvalidFunction("width_bucket(nan(), array[1.0, 2.0, 3.0])", "Operand cannot be NaN");
        assertInvalidFunction("width_bucket(3.14, array[0.0, infinity()])", "Bin value must be finite, got Infinity");

        // fail if we aren't sorted
        assertInvalidFunction("width_bucket(3.145, array[1.0, 0.0])", "Bin values are not sorted in ascending order");
        assertInvalidFunction("width_bucket(3.145, array[1.0, 0.0, -1.0])", "Bin values are not sorted in ascending order");
        assertInvalidFunction("width_bucket(3.145, array[1.0, 0.3, 0.0, -1.0])", "Bin values are not sorted in ascending order");

        // this is a case that we can't catch because we are using binary search to bisect the bins array
        assertFunction("width_bucket(1.5, array[1.0, 2.3, 2.0])", BIGINT, 1);
    }
}
