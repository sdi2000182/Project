package com.uoa.sensor2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class MyAdapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> ListOfFragments = new ArrayList<>();
    private final ArrayList<String> TitlesofFragments= new ArrayList<>();
    public MyAdapter(FragmentActivity fa) {
        super(fa);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ListOfFragments.get(position);
    }
    public CharSequence getTitleList(int position) {
        return TitlesofFragments.get(position);
    }


    public void addFragment(Fragment fragment, String title) {
        if(fragment ==null){
            System.out.println("ppppph\n");
        }
        System.out.println("mamam\n");
        ListOfFragments.add(fragment);
        System.out.println("meowww\n");
        TitlesofFragments.add(title);
    }
//      public String getTitle(int position) {
//          return TitlesofFragments.
//      }
//    public void addFragment2(String title) {
//
//        TitlesofFragments.add(title);
//    }
//    public void addFragment1(Fragment fragment) {
//        if(fragment ==null){
//            System.out.println("ppppph\n");
//        }
//        System.out.println("mamam\n");
//        ListOfFragments.add(fragment);
////        System.out.println("meowww\n");
//        TitlesofFragments.add(title);
//    }

    @Override
    public int getItemCount() {
        return ListOfFragments.size();
    }
}
