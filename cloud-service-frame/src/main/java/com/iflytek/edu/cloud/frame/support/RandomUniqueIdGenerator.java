/**
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iflytek.edu.cloud.frame.support;

import java.security.SecureRandom;

/**
 * @author @author libinsong1204@gmail.com
 * @date 2012-6-4 上午9:33:48
 */
public class RandomUniqueIdGenerator {
    private static final char[] PRINTABLE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012345679"
        .toCharArray();

    private static final int DEFAULT_MAX_RANDOM_LENGTH = 32;

    private static SecureRandom randomizer = new SecureRandom();

    public static String getNewString() {
        final byte[] random = getNewStringAsBytes();

        return convertBytesToString(random);
    }
    
    public static String getNewString(int length) {
        final byte[] random = getNewStringAsBytes(length);

        return convertBytesToString(random);
    }

    private static byte[] getNewStringAsBytes() {
    	return getNewStringAsBytes(DEFAULT_MAX_RANDOM_LENGTH);
    }

    private static byte[] getNewStringAsBytes(int maximumRandomLength) {
        final byte[] random = new byte[maximumRandomLength];

        randomizer.nextBytes(random);
        
        return random;
    }

    private static String convertBytesToString(final byte[] random) {
        final char[] output = new char[random.length];
        for (int i = 0; i < random.length; i++) {
            final int index = Math.abs(random[i] % PRINTABLE_CHARACTERS.length);
            output[i] = PRINTABLE_CHARACTERS[index];
        }

        return new String(output);
    }
}
