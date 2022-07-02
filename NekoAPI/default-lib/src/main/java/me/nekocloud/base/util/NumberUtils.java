/*
 *    Copyright 2022 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package me.nekocloud.base.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * @author whilein, _Novit_
 */
@UtilityClass
public class NumberUtils {

    private final DecimalFormat LONG_FORMATTER = new DecimalFormat("###,###");
    private final DecimalFormat DOUBLE_FORMATTER = new DecimalFormat("###,##0.00");

    public int getLongLength(final long value) {
        if (value < 1000000000) {
            if (value < 100000) {
                if (value < 100) {
                    if (value < 10) {
                        return 1;
                    } else {
                        return 2;
                    }
                } else {
                    if (value < 1000) {
                        return 3;
                    } else {
                        if (value < 10000) {
                            return 4;
                        } else {
                            return 5;
                        }
                    }
                }
            } else {
                if (value < 10000000) {
                    if (value < 1000000) {
                        return 6;
                    } else {
                        return 7;
                    }
                } else {
                    if (value < 100000000) {
                        return 8;
                    } else {
                        return 9;
                    }
                }
            }
        } else {
            if (value < 100000000000000L) {
                if (value < 100000000000L) {
                    if (value < 10000000000L) {
                        return 10;
                    } else {
                        return 11;
                    }
                } else {
                    if (value < 1000000000000L) {
                        return 12;
                    } else {
                        if (value < 10000000000000L) {
                            return 13;
                        } else {
                            return 14;
                        }
                    }
                }
            } else {
                if (value < 10000000000000000L) {
                    if (value < 1000000000000000L) {
                        return 15;
                    } else {
                        return 16;
                    }
                } else {
                    if (value < 100000000000000000L) {
                        return 17;
                    } else {
                        if (value < 1000000000000000000L) {
                            return 18;
                        } else {
                            return 19;
                        }
                    }
                }
            }
        }
    }

    public int getIntLength(final int value) {
        if (value < 100000) {
            if (value < 100) {
                if (value < 10) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                if (value < 1000) {
                    return 3;
                } else {
                    if (value < 10000) {
                        return 4;
                    } else {
                        return 5;
                    }
                }
            }
        } else {
            if (value < 10000000) {
                if (value < 1000000) {
                    return 6;
                } else {
                    return 7;
                }
            } else {
                if (value < 100000000) {
                    return 8;
                } else {
                    if (value < 1000000000) {
                        return 9;
                    } else {
                        return 10;
                    }
                }
            }
        }
    }

    public @NotNull String formatNumber(final double number) {
        return DOUBLE_FORMATTER.format(number);
    }

    public @NotNull String formatNumber(final long number) {
        return LONG_FORMATTER.format(number);
    }

    // спиздил у джарвиса
    public @NotNull OptionalLong parseLong(final @NotNull CharSequence input) {
        val length = input.length();

        if (length == 0) {
            return OptionalLong.empty();
        }

        final boolean negative;
        final int limit;

        int currentIndex = 0, digit;

        {
            val firstChar = input.charAt(0);

            if (firstChar < '0') {
                switch (firstChar) {
                    case '-':
                        negative = true;
                        limit = Integer.MIN_VALUE;
                        break;
                    case '+':
                        negative = false;
                        limit = -Integer.MAX_VALUE;
                        break;
                    default:
                        return OptionalLong.empty();
                }

                if (length == 1) {
                    return OptionalLong.empty();
                }

                currentIndex = 1;
                digit = digit(input.charAt(1));
            } else {
                negative = false;
                limit = -Integer.MAX_VALUE;
                digit = digit(firstChar);
            }
        }

        final int bound = limit / 10, lastIndex = length - 1;
        long negativeResult = 0;

        while (true) {
            if (negativeResult < bound) {
                return OptionalLong.empty();
            }

            negativeResult *= 10;

            if (negativeResult < limit + digit) {
                return OptionalLong.empty();
            }

            negativeResult -= digit;

            if (currentIndex == lastIndex) {
                break;
            }

            digit = digit(input.charAt(++currentIndex));
        }

        return OptionalLong.of(negative ? negativeResult : -negativeResult);
    }

    // и это тоже спиздил у него
    public @NotNull OptionalInt parseInt(final @NotNull CharSequence input) {
        val length = input.length();

        if (length == 0) {
            return OptionalInt.empty();
        }

        final boolean negative;
        final int limit;

        int currentIndex = 0, digit;

        {
            val firstChar = input.charAt(0);

            if (firstChar < '0') {
                switch (firstChar) {
                    case '-':
                        negative = true;
                        limit = Integer.MIN_VALUE;
                        break;
                    case '+':
                        negative = false;
                        limit = -Integer.MAX_VALUE;
                        break;
                    default:
                        return OptionalInt.empty();
                }

                if (length == 1) {
                    return OptionalInt.empty();
                }

                currentIndex = 1;
                digit = digit(input.charAt(1));
            } else {
                negative = false;
                limit = -Integer.MAX_VALUE;
                digit = digit(firstChar);
            }
        }

        final int bound = limit / 10, lastIndex = length - 1;
        int negativeResult = 0;

        while (true) {
            if (digit < 0 || negativeResult < bound) {
                return OptionalInt.empty();
            }

            negativeResult *= 10;

            if (negativeResult < limit + digit) {
                return OptionalInt.empty();
            }

            negativeResult -= digit;

            if (currentIndex == lastIndex) {
                break;
            }

            digit = digit(input.charAt(++currentIndex));
        }

        return OptionalInt.of(negative ? negativeResult : -negativeResult);
    }

    public int digit(final char value) {
        return value >= '0' && value <= '9' ? value & 0xF : -1;
    }

        /**
     * Добавить знак после каждой третьей цифры
     *
     * @param number - число
     * @param symbol - знак
     */
    public String spaced(int number, String symbol) {
        String integer = String.valueOf(number);
        StringBuilder builder = new StringBuilder();

        for (int a = 0; a < integer.length(); a++) {
            builder.append(integer.split("")[a]);

            if ((integer.length() - a + 2) % 3 != 0) {
                continue;
            }
            builder.append(symbol);
        }

        return builder.substring(0, builder.toString().length() - 1);
    }

    /**
     * Добавить запятую после каждой третьей цифры
     *
     * @param number - число
     */
    public String spaced(int number) {
        return spaced(number, ",");
    }
}
