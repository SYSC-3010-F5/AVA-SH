package f5.ava_sh;

/**
 * Created by Slate on 2017-03-13.
 */

public class CommandHelper {

    public String[] buttonNames = {
            "Terminal",
            "Ping Server",
            "New Alarm",
            "Request Time"

    };

    public CommandHelper(){

    }

    public String[] getCommands(){
        return buttonNames;
    }


}
