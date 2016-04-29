package com.dleal.linkfinder.component.main;

import com.dleal.linkfinder.component.base.Presenter;
import com.dleal.linkfinder.component.base.ProgressView;

import java.io.Serializable;

/**
 * Created by Daniel Leal on 29/04/16.
 */
public interface MainView extends Presenter.View, ProgressView {

    String getWebsiteURL();

    void showEmptyWebsiteURLError();

    void showWrongWebsiteURLFormatError();

}