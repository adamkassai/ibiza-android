package com.kassaiweb.ibiza;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kassaiweb.ibiza.Data.Group;
import com.kassaiweb.ibiza.Util.EncryptUtil;
import com.kassaiweb.ibiza.Util.SPUtil;

public class BarcodeFragment extends Fragment {

    private ImageView ivQR;

    public BarcodeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_barcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ivQR = view.findViewById(R.id.barcode_image);
        // final View contentView = view.findViewById(android.R.id.content);

        final String groupId = SPUtil.getString(Constant.CURRENT_GROUP_ID, "");
        FirebaseDatabase.getInstance().getReference("groups").child(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Group group = dataSnapshot.getValue(Group.class);
                        String reverseGroupName = EncryptUtil.reverse(groupId);
                        String reverseGroupPass = EncryptUtil.reverse(group.getPassword());
                        String qrStr = reverseGroupName + ',' + reverseGroupPass;
                        try {
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrStr, BarcodeFormat.QR_CODE, 400, 400);
                            ivQR.setImageBitmap(bitmap);
                        } catch(Exception e) {
                            e.printStackTrace();
                             // Snackbar.make(contentView, "Nem sikerült a QR kód generálás", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // TODO
                    }
                });
    }
}
