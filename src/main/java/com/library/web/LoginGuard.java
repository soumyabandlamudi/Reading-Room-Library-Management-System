package com.library.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Slows down password guessing: 5 wrong attempts lock that email + IP for 5 minutes. */
public final class LoginGuard {
    private static final int MAX_FAILS = 5;
    private static final long LOCK_MS = 5 * 60 * 1000L;
    private static final Map<String, long[]> STATE = new ConcurrentHashMap<>(); // key -> {fails, lockedUntil}

    private LoginGuard() {}

    public static String key(String email, String ip) { return email.toLowerCase() + "|" + ip; }

    /** @return milliseconds still locked, or 0 if the login may proceed */
    public static long lockedForMs(String key) {
        long[] s = STATE.get(key);
        if (s == null) return 0;
        long left = s[1] - System.currentTimeMillis();
        if (left <= 0 && s[1] != 0) { STATE.remove(key); return 0; }
        return Math.max(left, 0);
    }

    public static void fail(String key) {
        if (STATE.size() > 10000) STATE.clear();      // keep memory bounded
        STATE.compute(key, (k, s) -> {
            if (s == null) s = new long[2];
            s[0]++;
            if (s[0] >= MAX_FAILS) { s[0] = 0; s[1] = System.currentTimeMillis() + LOCK_MS; }
            return s;
        });
    }

    public static void success(String key) { STATE.remove(key); }
}
