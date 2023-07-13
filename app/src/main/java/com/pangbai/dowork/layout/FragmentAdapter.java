package com.pangbai.dowork.layout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {
    private  List<Fragment> mfragmentList;
    private List<String> mtitles;
    public  FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titles){
        super(fm);
        this.mfragmentList=fragmentList;
        this.mtitles=titles;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mfragmentList.get(position);
    }
    @Override
    public int getCount() {
        return mfragmentList.size();
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mtitles.get(position);
    }
}