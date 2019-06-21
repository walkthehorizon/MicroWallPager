package com.shentu.wallpaper.app;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.shentu.wallpaper.model.api.service.MicroService;

/**
 * 需要分页的Model继承该Model
 */
public abstract class BasePageModel extends BaseModel {
    protected int limit = MicroService.PAGE_LIMIT;
    protected int offset = MicroService.PAGE_START;

    public BasePageModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    protected int getOffset(boolean clear) {
        return offset = clear ? MicroService.PAGE_START : offset + limit;
    }
}
