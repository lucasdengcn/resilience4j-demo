package org.example.demo;

import java.math.BigDecimal;

/**
 * Instance size: 40 bytes
 */
public class Tick {

    // Instance size: 40 bytes
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal avg;
    //
    private long amount;
    private long volume;

    public Tick() {
        this.open = BigDecimal.valueOf(10.0);
        this.close = BigDecimal.valueOf(10.0);
        this.high = BigDecimal.valueOf(10.0);
        this.low = BigDecimal.valueOf(10.0);
        this.avg = BigDecimal.valueOf(10.0);
        this.amount = 10010000L;
        this.volume = 202020202020L;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public void setAvg(BigDecimal avg) {
        this.avg = avg;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public void reset(){

    }
}
