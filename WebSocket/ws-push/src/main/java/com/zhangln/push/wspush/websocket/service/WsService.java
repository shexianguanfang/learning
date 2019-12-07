package com.zhangln.push.wspush.websocket.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhangln.push.wspush.entity.LogWsConnectEntity;
import com.zhangln.push.wspush.service.ILogWsConnectService;
import com.zhangln.push.wspush.vo.WsRegVo;
import com.zhangln.push.wspush.websocket.WsRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sherry
 * @description
 * @date Create in 2019/12/7
 * @modified By:
 */
@Service
@Slf4j
public class WsService {

    @Autowired
    private ILogWsConnectService iLogWsConnectService;

    /**
     * 设备连接，未注册
     *
     * @param id
     */
    public void connected(String id) {
        LogWsConnectEntity logWsConnectEntity = LogWsConnectEntity.builder()
                .channelId(id)
                .status(0)
                .token("")
                .clientType("")
                .app("")
                .user("")
                .group("")
                .areaCode("")
                .country("CN")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        iLogWsConnectService.save(logWsConnectEntity);
    }

    /**
     * 设备注册成功
     *
     * @param id
     * @param tokenId
     * @param wsRegVo
     */
    public void regSuccess(String id, String tokenId, WsRegVo wsRegVo) {
//        不去更新connected时insert的数据，这里也是直接insert
        if (CollectionUtils.isEmpty(wsRegVo.getGroup())) {
            LogWsConnectEntity logWsConnectEntity = LogWsConnectEntity.builder()
                    .channelId(id)
                    .status(0)
                    .token(tokenId)
                    .clientType(wsRegVo.getClientType())
                    .app(wsRegVo.getApp())
                    .user(wsRegVo.getUser())
                    .group("")
                    .areaCode(wsRegVo.getAreaCode())
                    .country(StringUtils.isEmpty(wsRegVo.getCountry()) ? "CN" : wsRegVo.getCountry())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            iLogWsConnectService.save(logWsConnectEntity);
        } else {
            List<String> group = wsRegVo.getGroup();
            List<LogWsConnectEntity> connectEntities = new ArrayList<>(group.size());
            for (String groupName : group) {
                LogWsConnectEntity logWsConnectEntity = LogWsConnectEntity.builder()
                        .channelId(id)
                        .status(0)
                        .token(tokenId)
                        .clientType(wsRegVo.getClientType())
                        .app(wsRegVo.getApp())
                        .user(wsRegVo.getUser())
                        .group(groupName)
                        .areaCode(wsRegVo.getAreaCode())
                        .country(StringUtils.isEmpty(wsRegVo.getCountry()) ? "CN" : wsRegVo.getCountry())
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();
                connectEntities.add(logWsConnectEntity);
            }
            iLogWsConnectService.saveBatch(connectEntities);
        }
    }

    /**
     * 设备离线
     *
     * @param channelId
     */
    public void offLine(String channelId) {
        List<LogWsConnectEntity> list = iLogWsConnectService.list(new QueryWrapper<LogWsConnectEntity>()
                .eq(LogWsConnectEntity.CHANNEL_ID, channelId));
        List<LogWsConnectEntity> logWsConnectEntities = list.stream()
                .map(logWsConnectEntity -> logWsConnectEntity.setStatus(3))
                .collect(Collectors.toList());
        iLogWsConnectService.updateBatchById(logWsConnectEntities);
    }
}
