package com.cb.carbonbank;


import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;


public class ScanQrFragment extends Fragment {


    private static final String prefName = "AuthenticatedUser";
    private SharedPreferences sharedPreferences;

    public ScanQrFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_scan_qr, container, false);

        sharedPreferences = getActivity().getSharedPreferences(prefName,MODE_PRIVATE);
        String authUser = sharedPreferences.getString("authenticatedUser", "Anonymous");

        QRGEncoder qrgEncoder = new QRGEncoder(authUser, null, QRGContents.Type.TEXT, 350);

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
