package f5.ava_sh.Listeners;

/**
 *Class:             OnTextSetListener.java
 *Project:           AVA Smart Home
 *Author:            Nathaniel Charlebois
 *Date of Update:    23/02/2017
 *Version:           4.0.1
 *Git:               https://github.com/SYSC-3010-F5/AVA-SH
 *
 *Purpose:           A callback interface for naming fields in fragments
 *                   Sends data between
 *                      non-activity class->fragments->container activity
 *                   The Container Activity must implement this interface
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
 *  7: -defines play song
 *  8: -defines new temp
 *
 */

public interface OnTextSetListener {
    void onTextSet(int function,String name);
}
