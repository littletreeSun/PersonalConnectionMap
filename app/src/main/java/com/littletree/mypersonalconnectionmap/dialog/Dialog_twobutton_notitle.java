package com.littletree.mypersonalconnectionmap.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littletree.mypersonalconnectionmap.R;

/**
 * @ProjectName: MyPersonalConnectionMap
 * @Package: dialog
 * @ClassName: Dialog_twobutton_notitle
 * @Author: littletree
 * @CreateDate: 2022/7/21/021 14:31
 */
public class Dialog_twobutton_notitle extends Dialog {

    public Dialog_twobutton_notitle(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {private Context context;
        private TextView dialog_title;
        private TextView btn_one;
        private TextView btn_two;

        public Builder(Context context) {
            this.context = context;
        }

        public TextView getDialog_title() {
            return dialog_title;
        }

        public void setDialog_title(TextView dialog_title) {
            this.dialog_title = dialog_title;
        }

        public TextView getBtn_one() {
            return btn_one;
        }

        public void setBtn_one(TextView btn_one) {
            this.btn_one = btn_one;
        }

        public TextView getBtn_two() {
            return btn_two;
        }

        public void setBtn_two(TextView btn_two) {
            this.btn_two = btn_two;
        }

        public Dialog_twobutton_notitle create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final Dialog_twobutton_notitle dialog = new Dialog_twobutton_notitle(context, R.style.SunDialog);
            View layout = inflater.inflate(R.layout.dialog_twobutton_notitle, null);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);
            dialog_title = (TextView)layout.findViewById(R.id.dialog_title);
            btn_one = (TextView)layout.findViewById(R.id.btn_one);
            btn_two = (TextView)layout.findViewById(R.id.btn_two);
            return dialog;
        }
    }
}
