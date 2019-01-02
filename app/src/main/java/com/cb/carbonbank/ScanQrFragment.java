package com.cb.carbonbank;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScanQrFragment extends Fragment {


    public ScanQrFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_scan_qr, container, false);

        String privatekey="M1001";
        QRGEncoder qrgEncoder = new QRGEncoder(privatekey, null, QRGContents.Type.TEXT, 350);

        try {
            // Getting QR-Code as Bitmap
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            ImageView qrImage = view.findViewById(R.id.qrCodeImage);
            // Setting Bitmap to ImageView
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.toString();
        }

        return view;
    }

}
