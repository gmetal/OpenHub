

package com.thirtydegreesray.openhub.ui.fragment.base;

import com.thirtydegreesray.openhub.http.model.DefaultPaginator;
import com.thirtydegreesray.openhub.mvp.contract.base.IBaseContract;
import com.thirtydegreesray.openhub.ui.adapter.base.BaseAdapter;

/**
 * Created on 2017/7/20.
 *
 * @author ThirtyDegreesRay
 */

public abstract class ListFragment<P extends IBaseContract.Presenter, A extends BaseAdapter>
        extends AbstractListFragment<P, A, DefaultPaginator> {

//    private DefaultPaginator paginator = new DefaultPaginator();
}
