package f5.ava_sh;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.view.View;

/**
 * Created by Slate on 2017-02-24.
 */

public class MainActivityButtonListener implements OnClickListener {

    private String id;

    MainActivityButtonListener(String id){
        this.id = id;
    }

    @Override
    public void onClick(View view) {
        if (id == "Terminal") {
            Intent myIntent = new Intent(view.getContext(), TerminalActivity.class);
            view.getContext().startActivity(myIntent);
        }
    }


}
