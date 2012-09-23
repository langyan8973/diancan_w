package com.diancanw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.animationw.HistoryRotateAnim;
import com.customw.CategoryListAdapter;
import com.customw.CustomViewBinder;
import com.httpw.HttpDownloader;
import com.modelw.Desk;
import com.modelw.DeskType;
import com.modelw.Order;
import com.utilsw.DisplayUtil;
import com.utilsw.JsonUtils;
import com.utilsw.MenuUtils;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class main extends ActivityGroup {
    /** Called when the activity is first created. */
	public LinearLayout rootLayout;
	public LocalActivityManager activityManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        rootLayout=(LinearLayout)findViewById(R.id.table_continer);
		rootLayout.removeAllViews();
		activityManager = getLocalActivityManager();
		Intent intent=new Intent(main .this,TableList.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Window subActivity=getLocalActivityManager().startActivity("TableListPage",intent);
        View view=subActivity.getDecorView();
        rootLayout.addView(view);  

        LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
    }
    
    
}