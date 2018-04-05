/*
 * Copyright (c) 2018.  Group TBD, SPS2018, TUDelft
 * Author: Pradhayini Ramamurthy
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without
 * fee is hereby granted, provided that the above copyright notice and this permission notice appear
 * in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS
 * SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 * AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 * NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
 * OF THIS SOFTWARE.
 */

package com.tudelft.tbd.localization;

import android.support.annotation.NonNull;

/**
 * Couples RSSI measurements with corresponding BSSID
 */
public class Measurement implements Comparable<Measurement>{
    private String bssId;
    private Integer rssi;

    public String getBssId() { return bssId; }
    public Integer getRssi() { return rssi; }

    Measurement(String bssId, Integer rssi){
        this.bssId = bssId;
        this.rssi = rssi;
    }

    @Override
    public int compareTo(@NonNull Measurement measurement) {
        // To sort in descending order
        return measurement.rssi.compareTo(this.rssi);
    }
}
