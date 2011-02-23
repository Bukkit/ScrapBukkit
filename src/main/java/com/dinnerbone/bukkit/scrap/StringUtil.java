// $Id$
/*
 * Copyright (C) 2010 sk89q <http://www.sk89q.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the sk89q nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.dinnerbone.bukkit.scrap;

import java.util.Collection;

/**
 * String utilities.
 * 
 * @author sk89q
 */
public class StringUtil {
    /**
     * Trim a string if it is longer than a certain length.
     *  
     * @param str
     * @param len
     * @return
     */
    public static String trimLength(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len);
        }
        
        return str;
    }

    /**
     * Join an array of strings into a string.
     * 
     * @param str
     * @param delimiter
     * @param initialIndex
     * @return
     */
    public static String joinString(String[] str, String delimiter,
            int initialIndex) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(str[initialIndex]);
        for (int i = initialIndex + 1; i < str.length; i++) {
            buffer.append(delimiter).append(str[i]);
        }
        return buffer.toString();
    }

    /**
     * Join an array of strings into a string.
     * 
     * @param str
     * @param delimiter
     * @param initialIndex
     * @param quote 
     * @return
     */
    public static String joinQuotedString(String[] str, String delimiter,
            int initialIndex, String quote) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append(quote);
        buffer.append(str[initialIndex]);
        buffer.append(quote);
        for (int i = initialIndex + 1; i < str.length; i++) {
            buffer.append(delimiter).append(quote).append(str[i]).append(quote);
        }
        return buffer.toString();
    }

    /**
     * Join an array of strings into a string.
     * 
     * @param str
     * @param delimiter
     * @return
     */
    public static String joinString(String[] str, String delimiter) {
        return joinString(str, delimiter, 0);
    }

    /**
     * Join an array of strings into a string.
     * 
     * @param str
     * @param delimiter
     * @param initialIndex
     * @return
     */
    public static String joinString(Object[] str, String delimiter,
            int initialIndex) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(str[initialIndex].toString());
        for (int i = initialIndex + 1; i < str.length; i++) {
            buffer.append(delimiter).append(str[i].toString());
        }
        return buffer.toString();
    }

    /**
     * Join an array of strings into a string.
     * 
     * @param str
     * @param delimiter
     * @param initialIndex
     * @return
     */
    public static String joinString(int[] str, String delimiter,
            int initialIndex) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(Integer.toString(str[initialIndex]));
        for (int i = initialIndex + 1; i < str.length; i++) {
            buffer.append(delimiter).append(Integer.toString(str[i]));
        }
        return buffer.toString();
    }

    /**
     * Join an list of strings into a string.
     * 
     * @param str
     * @param delimiter
     * @param initialIndex
     * @return
     */
    public static String joinString(Collection<?> str, String delimiter,
            int initialIndex) {
        if (str.size() == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        int i = 0;
        for (Object o : str) {
            if (i >= initialIndex) {
                if (i > 0) {
                    buffer.append(delimiter);
                }
                
                buffer.append(o.toString());
            }
            i++;
        }
        return buffer.toString();
    }
}
