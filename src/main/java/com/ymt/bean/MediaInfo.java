package com.ymt.bean;

import com.ymt.domain.MediaBaseInfo;
import com.ymt.domain.MediaUrl;
import lombok.Data;

import java.util.List;

@Data
public class MediaInfo extends MediaBaseInfo {
    private List<MediaUrl> mediaUrls;
}
