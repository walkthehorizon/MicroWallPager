package com.jess.arms.mvp;

import com.jess.arms.integration.IRepositoryManager;

public class BasePageModel extends BaseModel {
    protected int page;

    public BasePageModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    public int getPage() {
        return page;
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    void onDestroy(LifecycleOwner owner) {
//        super.onDestroy(owner);
//        page = getPageStart();
//    }
//
//    protected abstract int getPageStart();
}
