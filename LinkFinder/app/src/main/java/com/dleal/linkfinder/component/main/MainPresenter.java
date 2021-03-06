package com.dleal.linkfinder.component.main;

import com.dleal.linkfinder.component.base.Presenter;
import com.dleal.linkfinder.model.WebLink;
import com.dleal.linkfinder.model.Website;
import com.dleal.linkfinder.usecase.FindLinksInWebsite;
import com.dleal.linkfinder.utils.ConnectivityUtils;
import com.dleal.linkfinder.utils.ValidationUtils;

import java.util.Collection;

import javax.inject.Inject;

import static com.dleal.linkfinder.utils.Constants.CONNECTION_TIMEOUT;
import static com.dleal.linkfinder.utils.Constants.CONNECTION_TIMEOUT_NS;

/**
 * Created by Daniel Leal on 29/04/16.
 */
public class MainPresenter extends Presenter<MainView> {

    private ConnectivityUtils connectivityUtils;
    private FindLinksInWebsite findLinksInWebsite;

    private MainView view;

    private String url;

    private long requestTime = -1;

    @Inject
    public MainPresenter(ConnectivityUtils connectivityUtils, FindLinksInWebsite findLinksInWebsite) {
        this.connectivityUtils = connectivityUtils;
        this.findLinksInWebsite = findLinksInWebsite;
    }

    @Override public void onCreate() {
        super.onCreate();
        this.view = getView();
    }

    public void onDownloadLinksClick() {
        startLinkSearch();
    }

    public void onRetryConnectionClick() {
        startLinkSearch();
    }

    public void onHTMLAvailable(String html) {
        findLinksInWebsite.getLinks(html, linksCallback);
    }

    public void onAlarm() {
        findLinksInWebsite.cancel();
        requestTime = -1;
        view.hideProgress();
        view.showTimeoutError();
    }

    private FindLinksInWebsite.Callback linksCallback = new FindLinksInWebsite.Callback() {
        @Override public void onSuccess(Collection<WebLink> links) {
            Website website = new Website(url, links);
            view.hideProgress();
            if (!hasTimeoutPassed()) {
                view.cancelAlarm();
                if (links.size() > 0)
                    view.navigateToLinkList(website);
                else
                    view.showNoLinksError();
            }
        }
    };

    private void startLinkSearch() {
        requestTime = System.nanoTime();

        url = view.getWebsiteURL();
        switch (ValidationUtils.checkURLValidity(url)) {
            case EMPTY:
                view.showEmptyWebsiteURLError();
                return;
            case NOT_VALID:
                view.showWrongWebsiteURLFormatError();
                return;
            case VALID:
                if (!connectivityUtils.isInternetAvailable()) {
                    view.showNoInternetError();
                    return;
                }
                view.setAlarm(CONNECTION_TIMEOUT);
                view.showProgress(null);
                view.loadUrl(url);
        }
    }

    private boolean hasTimeoutPassed() {
        long current = System.nanoTime();
        return current - requestTime >= CONNECTION_TIMEOUT_NS;
    }
}
