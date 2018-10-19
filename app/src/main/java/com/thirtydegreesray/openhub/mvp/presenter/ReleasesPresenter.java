package com.thirtydegreesray.openhub.mvp.presenter;

import com.apollographql.apollo.rx.RxApollo;
import com.thirtydegreesray.dataautoaccess.annotation.AutoAccess;
import com.thirtydegreesray.openhub.FetchRepositoriesQuery;
import com.thirtydegreesray.openhub.dao.DaoSession;
import com.thirtydegreesray.openhub.http.core.HttpObserver;
import com.thirtydegreesray.openhub.http.core.HttpResponse;
import com.thirtydegreesray.openhub.mvp.contract.IReleasesContract;
import com.thirtydegreesray.openhub.mvp.model.ModelTransformations;
import com.thirtydegreesray.openhub.mvp.model.Release;
import com.thirtydegreesray.openhub.mvp.presenter.base.BasePresenter;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.thirtydegreesray.openhub.mvp.model.ModelTransformations.transformRelease;

/**
 * Created by ThirtyDegreesRay on 2017/9/16 11:31:07
 */

public class ReleasesPresenter extends BasePresenter<IReleasesContract.View>
        implements IReleasesContract.Presenter {

    @AutoAccess String owner;
    @AutoAccess String repo;
    private ArrayList<Release> releases;

    @Inject
    public ReleasesPresenter(DaoSession daoSession) {

        super(daoSession);
    }

    @Override
    public void onViewInitialized() {

        super.onViewInitialized();
        if (releases == null) {
            loadReleases(1, false);
        } else {
            mView.showReleases(releases);
            mView.hideLoading();
        }
    }

    @Override
    public void loadReleases(final int page, final boolean isReload) {

        mView.showLoading();
        final boolean readCacheFirst = page == 1 && !isReload;
        final HttpObserver<FetchRepositoriesQuery.Data> httpObserver = new HttpObserver<FetchRepositoriesQuery.Data>() {

            @Override
            public void onError(Throwable error) {

                mView.hideLoading();
                mView.showLoadError(error.getMessage());
            }

            @Override
            public void onSuccess(HttpResponse<FetchRepositoriesQuery.Data> response) {

                mView.hideLoading();
                final FetchRepositoriesQuery.Data body = response.body();
                releases = new ArrayList<>();
                for (FetchRepositoriesQuery.Edge edge : body.repository().releases().edges()) {
                    releases.add(transformRelease(edge));
                }

                //if(response.body().size() == 0 && releases.size() != 0){
                //    mView.setCanLoadMore(false);
                //} else {
                mView.showReleases(releases);
                //}
            }
        };

        generalRxApolloHttpExecute(forceNetWork -> RxApollo.from(getApolloClient().query(
                FetchRepositoriesQuery.builder()
                        .owner(owner)
                        .name(repo)
                        .build())), httpObserver, readCacheFirst);

    }

    public String getRepoName() {

        return repo;
    }

    public String getOwner() {

        return owner;
    }
}
