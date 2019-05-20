package com.jess.arms.mvp;

import com.jess.arms.integration.IRepositoryManager;

public class BasePageModel extends BaseModel {
    protected int offset;
    protected int limit;

    public BasePageModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
        limit = 20;
    }

    public int updateOffset(boolean clear) {
        return clear ? 0 : offset + limit;
    }
}