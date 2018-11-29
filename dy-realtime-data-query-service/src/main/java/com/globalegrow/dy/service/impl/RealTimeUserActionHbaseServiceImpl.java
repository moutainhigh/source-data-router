package com.globalegrow.dy.service.impl;

import com.globalegrow.common.hbase.CommonHbaseMapper;
import com.globalegrow.dy.dto.UserActionData;
import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.dto.UserActionResponseDto;
import com.globalegrow.dy.enums.AppEventEnums;
import com.globalegrow.dy.service.RealTimeUserActionHbaseService;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class RealTimeUserActionHbaseServiceImpl implements RealTimeUserActionHbaseService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${app.hbase.table-name}")
    private String tableName;

    @Value("${app.hbase.column-family}")
    private String columnFamily;

    @Autowired
    private CommonHbaseMapper commonHbaseMapper;

    @Override
    public UserActionResponseDto getUserActionDataFromHbase(UserActionParameterDto userActionParameterDto){
        /*long start = System.currentTimeMillis();*/
        UserActionResponseDto userActionResponseDto = new UserActionResponseDto();
        Map<String, List<UserActionData>> data = new HashMap<>();
        final int[] totalSize = {0};
        List<String> inputType = userActionParameterDto.getType();
        if (inputType == null){
            inputType = new ArrayList<>();
        }
        if (inputType.size() < 1) {
            inputType.addAll(Arrays.stream(AppEventEnums.values()).map(AppEventEnums :: name).collect(Collectors.toList()));
        }

        /*for (int i = 0; i < inputType.size(); i++) {
            String eventName = inputType.get(i);
            StringBuilder rowkey =  new StringBuilder();
            rowkey = rowkey.append(userActionParameterDto.getSite().get(0)).append("_").append(eventName).append("_").append(userActionParameterDto.getCookieId());
            try {
                Result result = commonHbaseMapper.getRecordByRowKey(tableName,rowkey.toString(),userActionParameterDto.getSize());
                totalSize = result.size() + totalSize;
                Cell[] cells = result.rawCells();
                if (cells.length != 0) {
                    List<UserActionData> userActionDataList = new LinkedList<>();
                    for (Cell cell : cells) {
                        String columnValue = Bytes.toString(CellUtil.cloneValue(cell));
                        String[] keys = columnValue.split("_");
                        UserActionData userActionData = new UserActionData(keys[0],Long.valueOf(keys[1]));
                        userActionDataList.add(userActionData);
                    }
                    data.put(eventName,userActionDataList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        inputType.parallelStream().forEach(eventName -> {
            StringBuilder rowkey =  new StringBuilder();
            rowkey = rowkey.append(userActionParameterDto.getSite().get(0)).append("_").append(eventName).append("_").append(userActionParameterDto.getCookieId());
            try {
                Result result = commonHbaseMapper.getRecordByRowKey(tableName,rowkey.toString(),userActionParameterDto.getSize());
                totalSize[0] = result.size() + totalSize[0];
                Cell[] cells = result.rawCells();
                if (cells.length != 0) {
                    List<UserActionData> userActionDataList = new LinkedList<>();
                    for (Cell cell : cells) {
                        String columnValue = Bytes.toString(CellUtil.cloneValue(cell));
                        String[] keys = columnValue.split("_");
                        String eventTime = keys[1];
                        boolean eventTimeStatus1 = eventTime.contains("_");
                        boolean eventTimeStatus2 = eventTime.contains(".");
                        if(eventTimeStatus1 || eventTimeStatus2 || eventTime.length()<13){
                            if(eventTime.length()>=13){
                                eventTime = eventTime.substring(eventTime.length()-13);
                            }else{
                                eventTime = System.currentTimeMillis()+"";
                            }
                        }
                       /* System.out.println("keys[0]="+keys[0]);
                        System.out.println("eventTime="+eventTime);*/
                        UserActionData userActionData = new UserActionData(keys[0],Long.parseLong(eventTime));
                        userActionDataList.add(userActionData);
                    }
                    data.put(eventName,userActionDataList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        userActionResponseDto.setSize(totalSize[0]);
        userActionResponseDto.setData(data);
        /*System.out.println("花费：");
        System.out.println(System.currentTimeMillis() - start);*/

        return userActionResponseDto;
    }
}
