package com.example.whosin.model.Singleton;

import com.example.whosin.ui.Groups.MyGroupsFragment;
import com.example.whosin.ui.ProfileInfoFragment;
import com.example.whosin.ui.findGroup.FindGroupFragment;
import com.example.whosin.ui.home.HomeFragment;

public class Fragments {
    private static HomeFragment homeFragment;
    private static MyGroupsFragment MyGroupsFragment;
    private static FindGroupFragment findGroupFragment;
    private static ProfileInfoFragment settingFragment;

    private Fragments(){
    }

    public static HomeFragment getHomeFragment(){
        if (homeFragment == null){
            homeFragment = new HomeFragment();
        }
        return homeFragment;
    }
    public static MyGroupsFragment getMyGroupsFragment(){
        if (MyGroupsFragment == null){
            MyGroupsFragment = new MyGroupsFragment();
        }
        return MyGroupsFragment;
    }

    public static FindGroupFragment getFindGroupFragment() {
        if (findGroupFragment== null){
            findGroupFragment = new FindGroupFragment();
        }
        return findGroupFragment;
    }

    public static ProfileInfoFragment getSettingFragment() {
        if (settingFragment== null){
            settingFragment = new ProfileInfoFragment();
        }
        return settingFragment;
    }
}
