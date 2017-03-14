package f5.ava_sh;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import java.net.SocketException;

import network.DataChannel;
import network.NetworkException;
import network.PacketWrapper;

import android.app.AlertDialog;
import android.widget.EditText;

import static android.R.attr.data;
import static android.R.attr.id;

/**
 * Created by Slate on 2017-03-13.
 */

public class CommandHelper {

    public String[] buttonNames = {
            "terminal",
            "ping",
            "sch event",
            "set timer",
            "new alarm",
            "req current Weather",
            "req time",
            "req ip",
            "req np-events",
            "req p-events",
            "del np-event"
    };

    private AlertDialog.Builder alertDialogBuilder;
    final EditText et;
    private AlertDialog alertDialog;
    private DataChannel dataChannel;
    private MainActivity main;


    public CommandHelper(Context c, MainActivity main){
        this.main = main;
        dataChannel = main.getDataChannel();

        alertDialogBuilder = new AlertDialog.Builder(c);
        et = new EditText(c);



        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(et);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();
    }

    public String[] getCommands(){
        return buttonNames;
    }

    public void interpret(View view, String id){

        switch(id){
            case "terminal":
                Intent myIntent = new Intent(view.getContext(), TerminalActivity.class);
                view.getContext().startActivity(myIntent);
                break;

            case "ping":
                //declaring method variables
                long pre, post;

                int amount = 3;

                //ping 5 times
                for(int i=0; i<amount; i++) {
                    //pause between pinging
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    try {
                        //send ping
                        pre = System.currentTimeMillis();
                        dataChannel.sendCmd("ping");

                        //wait for response
                        PacketWrapper wrapper = dataChannel.receivePacket(5000);
                        if (wrapper.type == DataChannel.TYPE_INFO) {
                            post = System.currentTimeMillis();
                            et.append("Response from server, delay of " + (post - pre) + "ms" + "\n");
                        } else {
                            et.append("Unexpected packet received! " + "\n");
                        }
                    } catch (NetworkException e) {
                        et.append(e.getMessage()+"\n");
                    } catch (SocketException e) {
                        et.append("No response"+"\n");
                    }
                }

                // show it
                alertDialog.show();
                break;

            case "sch event":
                break;

            case "set timer":
                break;

            case "new alarm":
                break;

            case "req current weather":
                break;

            case "req time":
                break;

            case "req ip":
                break;

            case "req np-events":
                break;

            case  "req p-events":
                break;

            case "del np-event":
                break;

        }


    }


}
