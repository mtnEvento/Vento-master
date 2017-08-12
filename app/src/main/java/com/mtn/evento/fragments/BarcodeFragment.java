package com.mtn.evento.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

    RelativeLayout printable_surface;
    private Bitmap myBitmap;

    public BarcodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle bundle = getArguments();
        DisplayTicket displayTicket = (DisplayTicket) bundle.getSerializable(Constants.TICKET);

        View view = inflater.inflate(R.layout.fragment_barcode, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView transactionId = (TextView) view.findViewById(R.id.transactionId);
        Button shareBtn = (Button) view.findViewById(R.id.shareBtn);
        Button printBtn = (Button) view.findViewById(R.id.printBtn);
        shareBtn.setOnClickListener(this);
        printBtn.setOnClickListener(this);

        if(displayTicket != null){

           View print_preview = getActivity().getLayoutInflater().inflate(R.layout.print_preview,null);
           printable_surface = (RelativeLayout) print_preview.findViewById(R.id.printable_surface);
           ImageView imageQr = (ImageView) print_preview.findViewById(R.id.imageQr);

           TextView type_name = (TextView) print_preview.findViewById(R.id.type_name);
           TextView ptransactionId = (TextView) print_preview.findViewById(R.id.ptransactionId);


            Log.d(LOGMESSAGE, "display ticket: " + displayTicket);

            Log.d(LOGMESSAGE, "display code: " + displayTicket.getQrCode());

            EncodeData.getInstance();//  0xFF0000AA -bg :blue  rgb
            String encoded =  EncodeData.encode(displayTicket.getQrCode().trim());
            Bitmap myBitmap = QRCode.from( encoded).withColor(0x000000, 0xFd0012AC).bitmap();

            imageView.setImageBitmap(myBitmap);
            imageQr.setImageBitmap(myBitmap);
            name.setText(displayTicket.getName().toUpperCase());
            transactionId.setText(displayTicket.getTransactionId());
            ptransactionId.setText(displayTicket.getTransactionId());
            type_name.setText(displayTicket.getName().toUpperCase());
        }

        return view;


    }

//    TODO Print and Share Bitmap functionalities

    @Override
    public void onClick(View v) {
        printable_surface.setDrawingCacheEnabled(true);
        myBitmap = (Bitmap)printable_surface.getDrawingCache();

        switch (v.getId()){
            case R.id.printBtn:
                if (myBitmap != null) {
                    String absolutePath = Saver.saveBitmap(myBitmap,getActivity());
                    if(absolutePath != null){
                        Toast.makeText(getActivity(),"Ticket saved successfully",Toast.LENGTH_LONG).show();
                    }
                    Log.d(LOGMESSAGE, "saving failed: ");

                }
                break;
            case R.id.shareBtn:
                String result = SmartGet.printToFile(getActivity(),myBitmap);
                if(result != null){
                    Toast.makeText(getActivity(), "Ticket Shared", Toast.LENGTH_SHORT).show();
                    File file = new File(result);
                    if(file.exists()){
                        boolean deleted = file.delete();
                        if(!deleted){
                            Log.d(LOGMESSAGE, "file not deleted: ");
                        }else{
                            Log.d(LOGMESSAGE, "file deleted: ");
                        }
                    }
                }
                break;
        }
    }

    public boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getActivity(). getSystemService(getActivity().getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED)
        {

            return false;
        }
        return false;
    }
    private boolean isNetworkOn(){
        ConnectivityManager ConnectionManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true )
        {
            return true;
        }
        else
        {
            return  false;
        }
    }
    private boolean isNetworkAndInternetAvailable(){
        return  isNetworkOn()&& isInternetOn() ;
    }

}
