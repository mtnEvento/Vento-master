package com.mtn.evento.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mtn.evento.R;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.DisplayTicket;
import com.mtn.evento.data.EncodeData;
import com.mtn.evento.utils.Saver;
import com.mtn.evento.utils.SmartGet;

import net.glxn.qrgen.android.QRCode;

import org.w3c.dom.Text;

import java.io.File;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BarcodeFragment extends Fragment implements View.OnClickListener {

    LinearLayout printable_surface;
    View print_preview ;
    private Bitmap myBitmap;
    Button printBtn,shareBtn;

    public BarcodeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        DisplayTicket displayTicket = (DisplayTicket) bundle.getSerializable(Constants.TICKET);

        View view = inflater.inflate(R.layout.fragment_barcode, container, false);
        printable_surface = (LinearLayout) view.findViewById(R.id.print_view);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView transactionId = (TextView) view.findViewById(R.id.transactionId);
        shareBtn = (Button) view.findViewById(R.id.shareBtn);
        printBtn = (Button) view.findViewById(R.id.printBtn);
        shareBtn.setOnClickListener(this);
        printBtn.setOnClickListener(this);

        if(displayTicket != null){

            Log.d(LOGMESSAGE, "display ticket: " + displayTicket);
            Log.d(LOGMESSAGE, "display code: " + displayTicket.getQrCode());
            EncodeData.getInstance();//  0xFF0000AA -bg :blue  rgb
            String encoded =  EncodeData.encode(displayTicket.getQrCode().trim());
            //.withColor(0x000000, 0xFd0012AC)
            Bitmap myBitmap = QRCode.from( encoded).bitmap();
            imageView.setImageBitmap(myBitmap);
            name.setText(displayTicket.getName().toUpperCase());
            transactionId.setText(displayTicket.getTransactionId());
        }
        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.printBtn:
                printable_surface.post(new Runnable() {
                    @Override
                    public void run() {
                        myBitmap =   Saver.takeScreenShot(printable_surface);
                        Toast.makeText(getActivity(),"myBitmap : "+myBitmap,Toast.LENGTH_LONG).show();

                        String path = Saver.saveBitmap(myBitmap,getActivity());

                        if(path != null){
                            Saver.openScreenshot(path,getActivity());
                            Toast.makeText(getActivity(), "Ticket Shared", Toast.LENGTH_SHORT).show();
                            Log.d(LOGMESSAGE, "Ticket Shared");
                        }
                        else
                        {
                            Log.d(LOGMESSAGE, "not saved");
                        }
                    }
                });
                break;
            case R.id.shareBtn:


                printable_surface.post(new Runnable() {
                    @Override
                    public void run() {
                        myBitmap =   Saver.takeScreenShot(printable_surface);
                        Toast.makeText(getActivity(),"myBitmap : "+myBitmap,Toast.LENGTH_LONG).show();
                        String path = Saver.saveBitmap(myBitmap,getActivity());
                        String result = Saver.shareTicket(getActivity(),path);
                        if(result != null){
                            Toast.makeText(getActivity(), "Ticket Shared", Toast.LENGTH_SHORT).show();
                            Log.d(LOGMESSAGE, "Ticket Shared");
//                            File file = new File(result);
//                            if(file.exists()){
//                                boolean deleted = file.delete();
//                                if(!deleted){
//                                    Log.d(LOGMESSAGE, "file not deleted: ");
//                                }else{
//                                    Log.d(LOGMESSAGE, "file deleted: ");
//                                }
//                            }
                        }
                    }
                });

                break;
        }
    }
}
