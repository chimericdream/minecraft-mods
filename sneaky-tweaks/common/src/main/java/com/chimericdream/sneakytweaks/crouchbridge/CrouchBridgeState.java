package com.chimericdream.sneakytweaks.crouchbridge;

final class CrouchBridgeState {
    boolean hasAnchor;
    double anchorX;
    double anchorY;
    double anchorZ;

    // Set once a crossing can no longer be held (looked down, exceeded the gap limit, or stopped
    // crouching mid-air) so it can't be re-engaged until the player actually lands. A gap right at the
    // configured limit routinely trips this a single tick before the player's hitbox actually reaches
    // the far ledge and onGround() goes true — that one tick of real gravity is imperceptible, so it
    // shouldn't disqualify the advancement below; only lookedDown should.
    boolean fallCommitted;

    // Whether the current crossing has actually held the player up at least once, so a landing after
    // a trivial single-tick step doesn't award the advancement.
    boolean usedBridge;

    // Set only when fallCommitted was triggered by looking down, as opposed to exceeding the gap or
    // releasing crouch mid-air — the only reason a crossing should be denied the advancement.
    boolean lookedDown;
}
