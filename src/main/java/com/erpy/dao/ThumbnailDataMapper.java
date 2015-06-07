package com.erpy.dao;

import java.util.List;

/**
 * Created by baeonejune on 15. 6. 5..
 */
public interface ThumbnailDataMapper {
    void insertThumbnailData(ThumbnailData thumbnailData);
    ThumbnailData getThumbnailDataById(int dataId);
    ThumbnailData getFindThumbnailData(ThumbnailData thumbnailData);
    List<ThumbnailData> getAllThumbnailDatas();
    void updateThumbnailData(ThumbnailData thumbnailData);
    void deleteThumbnailData(int dataId);
}
