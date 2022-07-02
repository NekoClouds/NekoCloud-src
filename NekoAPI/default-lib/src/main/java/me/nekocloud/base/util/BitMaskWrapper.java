package me.nekocloud.base.util;

import com.google.common.base.MoreObjects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BitMaskWrapper {

    private int bitMask = 0;

    public BitMaskWrapper setState(int idx, boolean value) {
        bitMask = value ? (int)((long)bitMask | 1L << idx) :
                (int)((long)bitMask & (~(1L << idx)));

        return this;
    }

    public boolean getState(int idx) {
        return ((long)bitMask & 1L << idx) != 0L;
    }

    public BitMaskWrapper clone() {
        return new BitMaskWrapper(bitMask);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("mask",
                Integer.toBinaryString(bitMask)).toString();
    }
}

