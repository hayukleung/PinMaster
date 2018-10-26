package com.hayukleung.pinmaster.util;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.hayukleung.pinmaster.dialog.GameDialog;

import java.util.ArrayList;

/**
 * Created by hayukleung@gmail.com on 2018/10/26.
 */

public class DialogUtil {

    public static void show2(
            final String tag,
            FragmentManager fragmentManager,
            final String title,
            final String content,
            final String optNegative,
            final String optPositive,
            @NonNull final Listener2 listener) {

        DialogFragment fragmentByTag = (DialogFragment) fragmentManager.findFragmentByTag(tag);
        if (fragmentByTag != null) {
            fragmentByTag.dismissAllowingStateLoss();
        }
        ArrayList<String> array = new ArrayList<>(2);
        array.add(optNegative);
        array.add(optPositive);
        final GameDialog fragment = GameDialog.getInstance(fragmentManager, content, array, tag);
        fragment.show(fragmentManager);
        if (null != title) {
            fragment.setTitle(title);
        }
        fragment.addListener(0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNegativeClick();
                fragment.dismiss();
            }
        });
        fragment.addListener(1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPositiveClick();
                fragment.dismiss();
            }
        });
    }

    public interface Listener2 {

        void onNegativeClick();

        void onPositiveClick();
    }
}
