package f5.ava_sh.Listeners;

/**
 *Class:             OnTimeSetListener.java
 *Project:           AVA Smart Home
 *Author:            Nathaniel Charlebois
 *Date of Update:    23/02/2017
 *Version:           4.0.1
 *Git:               https://github.com/SYSC-3010-F5/AVA-SH
 *
 *Purpose:           Callback interface to pass data between
 *                      non-activity class->fragments->container activity
 *                   The Container Activity must implement this interface
 *
 */


public interface OnTimeSetListener {
    void onTimeSet(int[] time);
}
