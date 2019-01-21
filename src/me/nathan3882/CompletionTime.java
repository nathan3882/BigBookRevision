package me.nathan3882;

import java.util.concurrent.TimeUnit;

public class CompletionTime {

    private long completionTimeMillis;
    private boolean wasActuallyCompleted;

    public CompletionTime() {

    }
    public CompletionTime(long completionTimeMillis) {
        this.completionTimeMillis = completionTimeMillis;
    }

    public long inSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(inMillis());
    }

    public long inNano() {
        return TimeUnit.MILLISECONDS.toNanos(inMillis());
    }

    public long inMillis() {
        return this.completionTimeMillis;
    }

    public long getCompletionTimeMillis() {
        return completionTimeMillis;
    }

    public void setCompletionTimeMillis(long completionTimeMillis) {
        this.completionTimeMillis = completionTimeMillis;
    }

    public boolean isWasActuallyCompleted() {
        return wasActuallyCompleted;
    }

    public void setWasActuallyCompleted(boolean wasActuallyCompleted) {
        this.wasActuallyCompleted = wasActuallyCompleted;
    }
}
