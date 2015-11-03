package com.yopachara.health.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greenfrvr.rubberloader.RubberLoaderView;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;
import com.rey.material.app.ToolbarManager;
import com.rey.material.drawable.ThemeDrawable;
import com.rey.material.util.ThemeUtil;
import com.rey.material.util.ViewUtil;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.Switch;
import com.rey.material.widget.TabPageIndicator;
import com.yopachara.health.demo.Model.FoodModel;
import com.yopachara.health.demo.Model.HistoryModel;
import com.yopachara.health.demo.Model.UserModel;
import com.yopachara.health.demo.Service.HealthService;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class MainActivity extends AppCompatActivity implements ToolbarManager.OnToolbarGroupChangedListener {

    @Bind(R.id.main_dl)
    DrawerLayout dl_navigator;
    @Bind(R.id.main_fl_drawer)
    FrameLayout fl_drawer;
    AppBarLayout ab;
    private ListView lv_drawer;
    private CustomViewPager vp;
    private TabPageIndicator tpi;

    private DrawerAdapter mDrawerAdapter;
    private PagerAdapter mPagerAdapter;

    private Toolbar mToolbar;
    private ToolbarManager mToolbarManager;
    private SnackBar mSnackBar;

    private SpeechRecognizer sr;

    private static final String TAG = "Speech Recognizer";
    private Drawable[] mDrawables = new Drawable[2];
    private int index = 0;
    public ArrayList<UserModel.User> users;
//    protected @Bind(R.id.loader)
    RubberLoaderView l;
    FrameLayout frame_loader;


    String API = "http://pachara.me:3000";
    Dialog.Builder builder = null;
    boolean doubleBackToExitPressedOnce = false;

    private FloatingActionButton fab_line;

//    private Tab[] mItems = new Tab[]{Tab.HOME, Tab.FOODS, Tab.HISTORYS, Tab.PROGRESS, Tab.BUTTONS, Tab.FAB, Tab.SWITCHES, Tab.SLIDERS, Tab.SPINNERS, Tab.TEXTFIELDS, Tab.SNACKBARS, Tab.DIALOGS};
    private Tab[] mItems = new Tab[]{Tab.HOME, Tab.FOODS, Tab.HISTORYS, Tab.CHARTS, Tab.PROFILE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

//        ButterKnife.bind(this);
        l = (RubberLoaderView)findViewById(R.id.loader);
        l.startLoading();

        frame_loader = (FrameLayout)findViewById(R.id.frame_loader);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String username = bundle.getString("username");
            String password = bundle.getString("password");
            postLogin(username, password);
            String result = username;
            Toast.makeText(this, "Login complete : " + result, Toast.LENGTH_SHORT).show();
        }


        ab = (AppBarLayout) findViewById(R.id.appbar);
        lv_drawer = (ListView) findViewById(R.id.main_lv_drawer);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        vp = (CustomViewPager) findViewById(R.id.main_vp);
        tpi = (TabPageIndicator) findViewById(R.id.main_tpi);
        mSnackBar = (SnackBar) findViewById(R.id.main_sn);
        fab_line = (FloatingActionButton) findViewById(R.id.fab_line);
        fab_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawables[1] = v.getResources().getDrawable(R.drawable.ic_voice);
                mDrawables[0] = v.getResources().getDrawable(R.drawable.ic_done_white_24dp);
                fab_line.setLineMorphingState((fab_line.getLineMorphingState() + 1) % 2, true);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplication().getPackageName());
                sr.startListening(intent);
                Log.i("RecognizerIntent", "startListening");
            }

        });
        mToolbarManager = new ToolbarManager(getDelegate(), mToolbar, R.id.tb_group_main, R.style.ToolbarRippleStyle, R.anim.abc_fade_in, R.anim.abc_fade_out);
        mToolbarManager.setNavigationManager(new ToolbarManager.ThemableNavigationManager(R.array.navigation_drawer, getSupportFragmentManager(), mToolbar, dl_navigator) {
            @Override
            public void onNavigationClick() {
                if (mToolbarManager.getCurrentGroup() != R.id.tb_group_main)
                    mToolbarManager.setCurrentGroup(R.id.tb_group_main);
                else
                    dl_navigator.openDrawer(GravityCompat.START);
            }

            @Override
            public boolean isBackState() {
                return super.isBackState() || mToolbarManager.getCurrentGroup() != R.id.tb_group_main;
            }

            @Override
            protected boolean shouldSyncDrawerSlidingProgress() {
                return super.shouldSyncDrawerSlidingProgress() && mToolbarManager.getCurrentGroup() == R.id.tb_group_main;
            }

        });
        mToolbarManager.registerOnToolbarGroupChangedListener(this);

        mDrawerAdapter = new DrawerAdapter(this);
        lv_drawer.setAdapter(mDrawerAdapter);


        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());

//        l.setVisibility(View.VISIBLE);
//        frame_loader.setVisibility(View.VISIBLE);

    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }


        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        mSnackBar.applyStyle(R.style.SnackBarSingleLine)
                .text("Please click BACK again to exit")
                .duration(2000)
                .show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void postLogin(final String username, final String password) {
        String basicAuth = "Basic " + Base64.encodeToString(String.format("%s:%s", username, password).getBytes(), Base64.NO_WRAP);
        Log.d("Auth", basicAuth);
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);
        api.getUserID(basicAuth, username, new Callback<UserModel>() {
            @Override
            public void success(UserModel userModel, Response response) {
                users = userModel.getObjects();
                Log.d("Success Main", "user size " + users.size());
                int size = users.size() - 1;
                Log.d("Username", users.get(size).getUsername() + " " + users.get(size).getCreateAt());


                mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mItems, users.get(0));
                vp.setAdapter(mPagerAdapter);
                vp.setOffscreenPageLimit(5);
                tpi.setViewPager(vp);
                tpi.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int position) {
                        mDrawerAdapter.setSelected(mItems[position]);
                        mSnackBar.dismiss();
                        Log.d("onPageSelected", "Position " + position);
//                        float currentY = fab_line.getY();
                        switch (position) {
                            case 0: {
                                mToolbar.setTitle("Home");
                                fab_line.setY(750);
                                break;
                            }
                            case 1: {
                                mToolbar.setTitle("Foods");
                                fab_line.setY(1100);
                                break;
                            }
                            case 2: {
                                mToolbar.setTitle("History");
                                fab_line.setY(1100);
                                break;
                            }
                            case 3: {
                                mToolbar.setTitle("Statistic");
                                fab_line.setY(1100);
                                break;
                            }
                            case 4: {
                                mToolbar.setTitle("Profile");
                                fab_line.setY(1100);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {
//						Log.d("onPageScrolled","arg0: "+arg0+" arg1 "+arg1+" arg2 "+arg2);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        Log.d("SetPadding", "State " + state);
//							fab_line.setPadding(15,15,15,15);
                    }

                });

                mDrawerAdapter.setSelected(Tab.HOME);
                vp.setCurrentItem(0);

                ViewUtil.setBackground(getWindow().getDecorView(), new ThemeDrawable(R.array.bg_window));
                ViewUtil.setBackground(mToolbar, new ThemeDrawable(R.array.bg_toolbar));

//                l.setVisibility(View.GONE);
//                frame_loader.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Fail", error.toString());

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mToolbarManager.createMenu(R.menu.menu_main);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mToolbarManager.onPrepareMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tb_switch:
                mToolbarManager.setCurrentGroup(R.id.tb_group_contextual);
                break;
            case R.id.tb_done:
            case R.id.tb_done_all:
                mToolbarManager.setCurrentGroup(R.id.tb_group_main);
                break;
            case R.id.tb_theme:
                int theme = (ThemeManager.getInstance().getCurrentTheme() + 1) % ThemeManager.getInstance().getThemeCount();
                ThemeManager.getInstance().setCurrentTheme(theme);
                Toast.makeText(this, "Current theme: " + theme, Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onToolbarGroupChanged(int oldGroupId, int groupId) {
        mToolbarManager.notifyNavigationStateChanged();
    }

    public SnackBar getSnackBar() {
        return mSnackBar;
    }

    public enum Tab {
        HOME("Home"),
        FOODS("Foods"),
        HISTORYS("Historys"),
        CHARTS("Charts"),
        PROFILE("Profile"),
        PROGRESS("Progresses"),
        BUTTONS("Buttons"),
        FAB("FABs"),
        SWITCHES("Switches"),
        SLIDERS("Sliders"),
        SPINNERS("Spinners"),
        TEXTFIELDS("TextFields"),
        SNACKBARS("SnackBars"),
        DIALOGS("Dialogs");
        private final String name;

        private Tab(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName != null) && name.equals(otherName);
        }

        public String toString() {
            return name;
        }

    }

    class DrawerAdapter extends BaseAdapter implements View.OnClickListener, ThemeManager.OnThemeChangedListener {

        private Tab mSelectedTab;
        private int mTextColorLight;
        private int mTextColorDark;
        private int mBackgroundColorLight;
        private int mBackgroundColorDark;

        public DrawerAdapter(Context context) {
            mTextColorLight = context.getResources().getColor(R.color.abc_primary_text_material_light);
            mTextColorDark = context.getResources().getColor(R.color.abc_primary_text_material_dark);
            mBackgroundColorLight = ThemeUtil.colorPrimary(context, 0);
            mBackgroundColorDark = ThemeUtil.colorAccent(context, 0);

            ThemeManager.getInstance().registerOnThemeChangedListener(this);
        }

        @Override
        public void onThemeChanged(ThemeManager.OnThemeChangedEvent event) {
            notifyDataSetInvalidated();
        }

        public void setSelected(Tab tab) {
            if (tab != mSelectedTab) {
                mSelectedTab = tab;
                notifyDataSetInvalidated();
            }
        }

        public Tab getSelectedTab() {
            return mSelectedTab;
        }

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(MainActivity.this).inflate(R.layout.row_drawer, null);
                v.setOnClickListener(this);
            }

            v.setTag(position);
            Tab tab = (Tab) getItem(position);
            ((TextView) v).setText(tab.toString());

            if (tab == mSelectedTab) {
                v.setBackgroundColor(ThemeManager.getInstance().getCurrentTheme() == 0 ? mBackgroundColorLight : mBackgroundColorDark);
                ((TextView) v).setTextColor(0xFFFFFFFF);
            } else {
                v.setBackgroundResource(0);
                ((TextView) v).setTextColor(ThemeManager.getInstance().getCurrentTheme() == 0 ? mTextColorLight : mTextColorDark);
            }

            return v;
        }

        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            vp.setCurrentItem(position);
            dl_navigator.closeDrawer(fl_drawer);
        }
    }

    private static class PagerAdapter extends FragmentStatePagerAdapter {

        Fragment[] mFragments;
        Tab[] mTabs;
        UserModel.User user;
        private static final Field sActiveField;

        static {
            Field f = null;
            try {
                Class<?> c = Class.forName("android.support.v4.app.FragmentManagerImpl");
                f = c.getDeclaredField("mActive");
                f.setAccessible(true);
            } catch (Exception e) {
            }

            sActiveField = f;
        }

        public PagerAdapter(FragmentManager fm, Tab[] tabs, UserModel.User users) {
            super(fm);
            mTabs = tabs;
            mFragments = new Fragment[mTabs.length];
            user = users;


            //dirty way to get reference of cached fragment
            try {
                ArrayList<Fragment> mActive = (ArrayList<Fragment>) sActiveField.get(fm);
                if (mActive != null) {
                    for (Fragment fragment : mActive) {
                        if (fragment instanceof ProgressFragment)
                            setFragment(Tab.PROGRESS, fragment);
                        else if (fragment instanceof FoodFragment)
                            setFragment(Tab.FOODS, fragment);
                        else if (fragment instanceof HistoryFragment)
                            setFragment(Tab.HISTORYS, fragment);
                        else if (fragment instanceof ButtonFragment)
                            setFragment(Tab.BUTTONS, fragment);
                        else if (fragment instanceof FabFragment)
                            setFragment(Tab.FAB, fragment);
                        else if (fragment instanceof SwitchesFragment)
                            setFragment(Tab.SWITCHES, fragment);
                        else if (fragment instanceof SliderFragment)
                            setFragment(Tab.SLIDERS, fragment);
                        else if (fragment instanceof SpinnersFragment)
                            setFragment(Tab.SPINNERS, fragment);
                        else if (fragment instanceof TextfieldFragment)
                            setFragment(Tab.TEXTFIELDS, fragment);
                        else if (fragment instanceof SnackbarFragment)
                            setFragment(Tab.SNACKBARS, fragment);
                        else if (fragment instanceof DialogsFragment)
                            setFragment(Tab.DIALOGS, fragment);
                        else if (fragment instanceof HomeFragment)
                            setFragment(Tab.HOME, fragment);
                        else if (fragment instanceof ChartFragment)
                            setFragment(Tab.CHARTS, fragment);
                        else if (fragment instanceof ProfileFragment)
                            setFragment(Tab.PROFILE, fragment);
                    }
                }
            } catch (Exception e) {
            }
        }

        private void setFragment(Tab tab, Fragment f) {
            for (int i = 0; i < mTabs.length; i++)
                if (mTabs[i] == tab) {
                    mFragments[i] = f;
                    break;
                }
        }

        @Override
        public Fragment getItem(int position) {
            if (mFragments[position] == null) {
                switch (mTabs[position]) {
                    case PROGRESS:
                        mFragments[position] = ProgressFragment.newInstance();
                        break;
                    case BUTTONS:
                        mFragments[position] = ButtonFragment.newInstance();
                        break;
                    case FAB:
                        mFragments[position] = FabFragment.newInstance();
                        break;
                    case SWITCHES:
                        mFragments[position] = SwitchesFragment.newInstance();
                        break;
                    case SLIDERS:
                        mFragments[position] = SliderFragment.newInstance();
                        break;
                    case SPINNERS:
                        mFragments[position] = SpinnersFragment.newInstance();
                        break;
                    case TEXTFIELDS:
                        mFragments[position] = TextfieldFragment.newInstance();
                        break;
                    case SNACKBARS:
                        mFragments[position] = SnackbarFragment.newInstance();
                        break;
                    case DIALOGS:
                        mFragments[position] = DialogsFragment.newInstance();
                        break;
                    case HOME:
                        mFragments[position] = HomeFragment.newInstance(user);
                        break;
                    case FOODS:
                        mFragments[position] = FoodFragment.newInstance();
                        break;
                    case HISTORYS:
                        mFragments[position] = HistoryFragment.newInstance();
                        break;
                    case CHARTS:
                        mFragments[position] = ChartFragment.newInstance();
                        break;
                    case PROFILE:
                        mFragments[position] = ProfileFragment.newInstance(user);
                        break;
                }
            }

            return mFragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs[position].toString().toUpperCase();
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }

    private void postSearch(String txt) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);

        api.postSearch(txt, new Callback<FoodModel>() {


            @Override
            public void success(FoodModel foodModel, Response response) {
                ArrayList<FoodModel.Foods> text = foodModel.getObjects();
                //Log.d("Response",text.get(0).getName());
                createDialog(text);
                Snackbar.make(getWindow().getDecorView().getRootView(), "Hello Snackbar", Snackbar.LENGTH_LONG).show();
                Log.d("Success", response.getBody().toString());
            }

            @Override
            public void failure(RetrofitError error) {
                mSnackBar.applyStyle(R.style.SnackBarSingleLine)
                        .text(error.toString())
                        .show();
                Log.d("Error", error.toString());

            }
        });
    }

    private void postHistory(String foodname, String username, float cal, float carbo, float fat, float protein) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);

        api.postHistory(username, foodname, cal, protein, carbo, fat, new Callback<HistoryModel>() {
            @Override
            public void success(HistoryModel historyModel, Response response) {
                Log.d("Success", response.getBody().toString());
                mSnackBar.applyStyle(R.style.SnackBarSingleLine)
                        .text(response.toString())
                        .show();

                Fragment currentFragment = (Fragment) vp.getAdapter().instantiateItem(vp, 0);//gets current fragment
                //now you have to cast it to your fragment, let's say it's name is SenapatiFragment

                ((HomeFragment) currentFragment).getRefreshDeco();
            }

            @Override
            public void failure(RetrofitError error) {
                mSnackBar.applyStyle(R.style.SnackBarSingleLine)
                        .text(error.toString())
                        .show();
                Log.d("Error", error.toString());
            }
        });
    }

    private void createDialog(final ArrayList<FoodModel.Foods> dialog) {

        boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;

        builder = new SimpleDialog.Builder(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                //postSearch(getSelectedValue().toString());
                //TODO: Do correct username
                postHistory(dialog.get(getSelectedIndex()).getName(),
                        "yopachara",
                        dialog.get(getSelectedIndex()).getCal(),
                        dialog.get(getSelectedIndex()).getCarbo(),
                        dialog.get(getSelectedIndex()).getFat(),
                        dialog.get(getSelectedIndex()).getProtein());

                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };
//		CharSequence[] cs = new CharSequence[dialog.size()];
//		dialog.toArray(cs);


        ArrayList<String> listing = new ArrayList<String>();
        FoodModel.Foods item;
        for (int i = 0; i < dialog.size(); i++) {
            listing.add(dialog.get(i).getName());
            //getPath is a method in the customtype class which will return value in string format

        }
        final CharSequence[] cs = listing.toArray(new CharSequence[listing.size()]);


        ((SimpleDialog.Builder) builder).items(cs, 0)
                .title("รายการอาหาร")
                .positiveAction("เลือก")
                .negativeAction("ยกเลิก");
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), null);

        mSnackBar.applyStyle(R.style.SnackBarSingleLine)
                .text("results: " + String.valueOf(dialog))
                .show();
    }

    class listener implements RecognitionListener {
        private MainActivity mActivity;
        Dialog.Builder builder = null;

        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");
        }

        public void onError(int error) {
            Log.d(TAG, "error " + error);
            if (error == 4) {
                mSnackBar.applyStyle(R.style.SnackBarSingleLine)
                        .text("โปรดเชื่อมต่ออินเตอร์เน็ต")
                        .show();
            }
//			mSnackBar.applyStyle(R.style.SnackBarSingleLine)
//					.text("error " + error)
//					.show();
        }

        public void onResults(Bundle results) {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            final ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++) {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }
            boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;

            builder = new SimpleDialog.Builder(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog) {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    Toast.makeText(MainActivity.this, "คุณได้เลือก " + getSelectedValue(), Toast.LENGTH_SHORT).show();
                    postSearch(getSelectedValue().toString());
                    super.onPositiveActionClicked(fragment);
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    Toast.makeText(MainActivity.this, "ยกเลิก", Toast.LENGTH_SHORT).show();
                    super.onNegativeActionClicked(fragment);
                }
            };
            CharSequence[] cs = new CharSequence[data.size()];
            data.toArray(cs);

            ((SimpleDialog.Builder) builder).items(cs, 0)
                    .title("รายการอาหาร")
                    .positiveAction("เลือก")
                    .negativeAction("ยกเลิก");
            DialogFragment fragment = DialogFragment.newInstance(builder);
            fragment.show(getSupportFragmentManager(), null);

            mSnackBar.applyStyle(R.style.SnackBarSingleLine)
                    .text("results: " + String.valueOf(data))
                    .show();
        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }
}
