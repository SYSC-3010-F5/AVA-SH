package f5.ava_sh.Listeners;

/**
 * Created by Slate on 2017-04-01.
 *
 * A callback interface for naming fields
 *
 *
 * TYPE protocol:
 *  0: -defines a timerName
 *  1: -defines an alarmName
 *
 *
 */

public interface OnTextSetListener {
    public void onTextSet(int function,String name);
}
