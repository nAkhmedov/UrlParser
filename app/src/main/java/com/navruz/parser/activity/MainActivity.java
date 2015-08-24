package com.navruz.parser.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.navruz.parser.R;
import com.navruz.parser.constants.ContextConstants;
import com.navruz.parser.execution.CustomHTTPService;
import com.navruz.parser.fragment.ProgressDialogFragment;
import com.navruz.parser.util.LenientGsonConverter;
import com.navruz.parser.util.NetworkStateUtil;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();
    public static CustomHTTPService http;

    @ViewById(R.id.firstTextView)
    public EditText first10SymbolView;

    @ViewById(R.id.secondTextView)
    public EditText each10SymbolByCommaView;

    @ViewById(R.id.thirdTextView)
    public EditText countAutoView;

    @Bean
    ProgressDialogFragment progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(item.getItemId()) {
            case R.id.action_clear:
                clearAllViews();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearAllViews() {
        first10SymbolView.setText("");
        each10SymbolByCommaView.setText("");
        countAutoView.setText("");
    }

    public void parseDOM(View view) {
        NetworkStateUtil networkStateUtil = new NetworkStateUtil();
        if (networkStateUtil.isNone(MainActivity.this)) {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
            return;
        }
        showDialog();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ContextConstants.GET_DOM_IP_URL)
                .setConverter(new LenientGsonConverter(gson))
                .build();
        http = restAdapter.create(CustomHTTPService.class);
        http.getAutoResponse(new Callback<String>() {
            @Override
            public void success(String dom, Response response) {
                if (response.getStatus() == 200) {
                    doFirstTask(dom);
                    doSecondTask(dom);
                    doThirdTask(dom);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getMessage());
            }
        });


    }

    @Background
    public void doFirstTask(String dom) {
        showResult(first10SymbolView, dom.substring(0, 10));
    }

    @Background
    public void doSecondTask(String dom) {
        int commaInterval = 10;
        char separator = ',';
        StringBuilder sb = new StringBuilder(dom);

        int length = dom.length() / commaInterval;
        for(int i = 0; i < length; i++) {
            sb.insert(((i + 1) * commaInterval) + i, separator);
        }
        showResult(each10SymbolByCommaView, sb.toString());
    }

    @Background
    public void doThirdTask(String dom) {
        int count = 0;
        Pattern pattern = Pattern.compile("(.*авто)");
        Matcher matcher = pattern.matcher(dom.toLowerCase());
        while (matcher.find()) {
            count++;
        }
        showResult(countAutoView, count + "");
        dismissDialog();
    }

    @UiThread
    public void showResult(EditText view, String result) {
        view.setText(result);
    }

    public void showDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prevFragment = getSupportFragmentManager().findFragmentByTag("progress");
        if (prevFragment == null) {
            progressDialog.show(ft, "progress");
        }
    }

    @UiThread
    public void dismissDialog() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag("progress");
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }
}
