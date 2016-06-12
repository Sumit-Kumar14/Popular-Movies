package com.infinity.dev.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity implements OnItemSelectedListener{

    private boolean mTwoPane;

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    SlidingTabLayout tabs;
    Fragment popular, topRated, favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle popularData = new Bundle();
        popularData.putString("TYPE", "POPULAR");
        popular = new PlaceholderFragment();
        popular.setArguments(popularData);

        Bundle topRatedData = new Bundle();
        topRatedData.putString("TYPE", "TOP_RATED");
        topRated = new PlaceholderFragment();
        topRated.setArguments(topRatedData);

        Bundle favData = new Bundle();
        favData.putString("TYPE", "FAVOURITE");
        favourite = new PlaceholderFragment();
        favourite.setArguments(favData);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        initFragments();

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
    }

    private void initFragments(){

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mSectionsPagerAdapter.addFragment(popular);
        mSectionsPagerAdapter.addFragment(topRated);
        mSectionsPagerAdapter.addFragment(favourite);

        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        if (tabs != null) {
            tabs.setDistributeEvenly(true);
        }

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.yellow);
            }
        });

        tabs.setViewPager(mViewPager);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments;

        private String []title = {"POPULAR", "TOP RATED", "FAVOURITE"};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
        }

        public void addFragment(android.support.v4.app.Fragment fragment){
            fragments.add(fragment);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }

    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString("ID", id);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            Context context = this;
            Intent intent = new Intent(context, MovieDetail.class);
            intent.putExtra("ID", id);

            context.startActivity(intent);
        }
    }
}