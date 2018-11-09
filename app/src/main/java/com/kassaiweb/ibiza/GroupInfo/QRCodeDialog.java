package com.kassaiweb.ibiza.GroupInfo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kassaiweb.ibiza.R;
import com.kassaiweb.ibiza.Util.EncryptUtil;

public class QRCodeDialog extends Dialog {

    private String groupId, password;

    public QRCodeDialog(@NonNull Context context, String groupId, String password) {
        super(context);
        this.groupId = groupId;
        this.password = password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qr_code_dialog);
        ImageView ivQR = findViewById(R.id.qr_code_dialog_image);
        TextView tvClose = findViewById(R.id.qr_code_dialog_close);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeDialog.this.dismiss();
            }
        });

        String reverseGroupName = EncryptUtil.reverse(groupId);
        String reverseGroupPass = EncryptUtil.reverse(password);
        String qrStr = reverseGroupName + ',' + reverseGroupPass;
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrStr, BarcodeFormat.QR_CODE, 500, 500);
            ivQR.setImageBitmap(bitmap);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
