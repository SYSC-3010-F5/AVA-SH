package f5.ava_sh.Listeners;

import android.content.Context;
import android.view.View.OnClickListener;
import android.view.View;

import f5.ava_sh.CommandHelper;
import f5.ava_sh.MainActivity;

/**
 * Created by Slate on 2017-02-24.
 */

public class MainActivityButtonListener implements OnClickListener {

    private String id;
    private CommandHelper commandHelper;
    Context c;
    MainActivity main;

    MainActivityButtonListener(String id, Context c, MainActivity main){
        this.id = id;
        this.c = c;
        this.main = main;
        commandHelper = new CommandHelper(c, main);
    }

    @Override
    public void onClick(View view) {
        commandHelper.interpret(view, id);
    }


}
