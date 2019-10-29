package com.example.chatapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectiePagerAdapter extends FragmentPagerAdapter {
    // Adapter voor de frags.

    public SectiePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        // Tel de positie waar op is getikt...

        switch (position)
        {
            case 0:
                VerzoekFrag verzoekFrag = new VerzoekFrag();
                return  verzoekFrag;
            case 1:
                GesprekkenFrag gesprekkenFrag = new GesprekkenFrag();
                return  gesprekkenFrag;
            case 2:
                VriendenFrag vriendenFrag = new VriendenFrag();
                return vriendenFrag;
            default:
                return null;


        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Verzoeken";
            case 1:
                return "Berichten";
            case 2:
                return "Vrienden";
            default:
                return null;
        }
    }
}


