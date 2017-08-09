package com.mtn.evento.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mtn.evento.R;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.DisplayTicket;
import com.mtn.evento.data.EncodeData;

import net.glxn.qrgen.android.QRCode;

import org.w3c.dom.Text;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BarcodeFragment extends Fragment implements View.OnClickListener {


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

        if(displayTicket != null){

            Log.d(LOGMESSAGE, "display ticket: " + displayTicket);

            Log.d(LOGMESSAGE, "display code: " + displayTicket.getQrCode());

            EncodeData.getInstance();//  0xFF0000AA -bg :blue  rgb
            String encoded =  EncodeData.encode(displayTicket.getQrCode().trim());
            Bitmap myBitmap = QRCode.from( encoded).withColor(0x000000, 0xFd0012AC).bitmap();

            imageView.setImageBitmap(myBitmap);
            name.setText(displayTicket.getName());
            transactionId.setText(displayTicket.getTransactionId());
        }

        return view;


    }

//    TODO Print and Share Bitmap functionalities

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.printBtn:
                Toast.makeText(getContext(), "Print", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share:
                Toast.makeText(getContext(), "Share", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
