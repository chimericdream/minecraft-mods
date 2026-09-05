package com.chimericdream.betterportallinking.portal;

/**
 * Carries the entry portal's {@link PortalAddress} from
 * {@code NetherPortalBlock#getPortalDestination} (where the entry portal is known) to
 * {@code PortalForcer#findClosestPortalPosition} (where the exit candidate search happens) without
 * changing either method's signature.
 *
 * <p>A {@link ThreadLocal} is the carrier — portal handling only ever runs on the server thread, so
 * a plain static field would work in practice, but {@code ThreadLocal} costs nothing here and
 * removes the question entirely.
 *
 * <p><b>Two things keep a stale address from affecting an unrelated search.</b> {@link #take()}
 * reads and clears in one operation, so a value is consumed by the search it was recorded for. That
 * alone is not sufficient: if the search is never reached — another mod cancels
 * {@code getPortalDestination} at HEAD, or the call throws — neither {@code take()} nor the
 * {@code RETURN}-site {@link #clear()} runs, and the value survives. So the recording site also
 * writes this context on <em>every</em> path, storing an empty address rather than skipping the
 * write. A leftover can therefore never be read by a later transit, because that transit overwrites
 * it before the search runs.
 */
public final class EntryPortalContext {
    private static final ThreadLocal<PortalAddress> CONTEXT = new ThreadLocal<>();

    private EntryPortalContext() {
    }

    /**
     * Records the entry portal's address for the upcoming exit-portal search on this thread. Call
     * this on every path — including with {@link PortalAddress#empty()} — rather than skipping the
     * write, so no earlier value can survive into this transit.
     */
    public static void set(PortalAddress address) {
        CONTEXT.set(address);
    }

    /**
     * Returns the currently recorded address and clears it in the same operation. If nothing was
     * recorded, returns {@link PortalAddress#empty()} rather than {@code null} so callers never
     * need a separate null check.
     */
    public static PortalAddress take() {
        PortalAddress address = CONTEXT.get();
        CONTEXT.remove();
        return address != null ? address : PortalAddress.empty();
    }

    /** Clears any recorded address without returning it. Safe to call when nothing is set. */
    public static void clear() {
        CONTEXT.remove();
    }
}
