package com.example.whosin.model;

import androidx.fragment.app.Fragment;

public class ImageResources {
    private int imageR;
    private int backR;
    private Fragment fragmentToGo;

    public ImageResources(int imageR, int backR, Fragment fragmentToGo) {
        this.imageR = imageR;
        this.backR = backR;
        this.fragmentToGo = fragmentToGo;
    }

    public int getImageR() {
        return imageR;
    }

    public void setImageR(int imageR) {
        this.imageR = imageR;
    }

    public int getBackR() {
        return backR;
    }

    public void setBackR(int backR) {
        this.backR = backR;
    }

    public Fragment getFragmentToGo() {
        return fragmentToGo;
    }

    public void setFragmentToGo(Fragment fragmentToGo) {
        this.fragmentToGo = fragmentToGo;
    }
}
