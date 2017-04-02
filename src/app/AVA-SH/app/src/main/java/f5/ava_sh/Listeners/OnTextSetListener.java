package f5.ava_sh.Listeners;

/**
 * Created by Slate on 2017-04-01.
 *
 * A callback interface for naming fields
 *
 *
 * TYPE protocol:
 *  0: -defines a timerName
 *  1: -defines an location
 *  2: -defines an IP request
 *  3: -defines del NP-event
 *  4: -defines del P-event
 *  5: -defines details NP-event
 *  6: -defines details P-event
 *
 */

public interface OnTextSetListener {
    public void onTextSet(int function,String name);
}
