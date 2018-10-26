package com.hayukleung.pinmaster.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hayukleung.pinmaster.R;
import com.hayukleung.pinmaster.util.ScreenUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by hayukleung@gmail.com on 2018/10/26.
 */

public class GameDialog extends AppCompatDialogFragment {

    public static final int DEFAULT_INDEX_CANCEL = 0;
    public static final int DEFAULT_INDEX_CONFIRM = 1;

    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_BUTTONS = "buttons";
    private static final String KEY_TAG = "tag";
    private static final String HTML_PATTERN = "</font>";
    @NonNull
    private SparseArray<View.OnClickListener> mOnClickListeners = new SparseArray<>();
    private String mTag;

    public static GameDialog getInstance(@NonNull FragmentManager fragmentManager, @NonNull String content, @NonNull ArrayList<String> buttons, @NonNull String tag) {
        GameDialog fragment = (GameDialog) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new GameDialog();
            Bundle args = new Bundle();
            args.putString(KEY_CONTENT, content);
            args.putStringArrayList(KEY_BUTTONS, buttons);
            args.putString(KEY_TAG, tag);
            fragment.setArguments(args);
        }
        return fragment;
    }

    public static GameDialog getInstance(@NonNull FragmentManager fragmentManager, @NonNull String content, @NonNull String[] buttons, @NonNull String tag) {
        GameDialog fragment = (GameDialog) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new GameDialog();
            Bundle args = new Bundle();
            args.putString(KEY_CONTENT, content);
            args.putStringArrayList(KEY_BUTTONS, new ArrayList<>(Arrays.asList(buttons)));
            args.putString(KEY_TAG, tag);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.GameDialogTheme);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == getContext()) {
            return null;
        }
        if (null == getArguments()) {
            return null;
        }
        View view = inflater.inflate(R.layout.dialog_game, container, false);
        String content = getArguments().getString(KEY_CONTENT);
        ArrayList<String> buttons = getArguments().getStringArrayList(KEY_BUTTONS);
        mTag = getArguments().getString(KEY_TAG);
        if (!TextUtils.isEmpty(content)) {
            if (content.contains(HTML_PATTERN)) {
                Spanned spanned = Html.fromHtml(content);
                ((AppCompatTextView) view.findViewById(R.id.content)).setText(spanned);
            } else {
                ((AppCompatTextView) view.findViewById(R.id.content)).setText(content);
            }
        }
        String title = getArguments().getString(KEY_TITLE);
        AppCompatTextView titleView = view.findViewById(R.id.title);
        if (!TextUtils.isEmpty(title)) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title);
        } else {
            titleView.setVisibility(View.GONE);
        }
        if (buttons != null) {
            LinearLayoutCompat buttonLayout = view.findViewById(R.id.button_layout);
            int size = buttons.size();
            for (int i = 0; i < size; i++) {
                String buttonName = buttons.get(i);
                View buttonView = inflater.inflate(R.layout.view_dialog_game_button, buttonLayout, false);
                TextView button = buttonView.findViewById(R.id.button);

                if (mOnClickListeners.get(i) != null) {
                    button.setOnClickListener(mOnClickListeners.get(i));
                } else {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                }
                button.setText(buttonName);
                if (buttonView.getLayoutParams() instanceof LinearLayoutCompat.LayoutParams) {
                    LinearLayoutCompat.LayoutParams lp = (LinearLayoutCompat.LayoutParams) buttonView.getLayoutParams();
                    lp.weight = 1;
                    buttonLayout.addView(buttonView, lp);
                } else {
                    buttonLayout.addView(buttonView);
                }
            }
        }
        setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, 0);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getDialog().getWindow().setLayout((int) (ScreenUtil.getScreenWidth() * 0.7), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void addListener(int index, @NonNull View.OnClickListener onClickListener) {
        mOnClickListeners.append(index, onClickListener);
    }

    public void setTitle(String title) {
        Bundle bundle = getArguments();
        if (null == bundle) {
            return;
        }
        bundle.putString(KEY_TITLE, title);
    }

    public void show(FragmentManager manager) {
        this.show(manager, mTag);
    }
}

