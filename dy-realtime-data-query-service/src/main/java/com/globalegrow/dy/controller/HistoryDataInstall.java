package com.globalegrow.dy.controller;

import com.globalegrow.dy.utils.FileUtil;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.params.Parameters;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("history")
public class HistoryDataInstall {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    @Qualifier("myJestClient")
    private JestClient jestClient;

    @GetMapping
    public String install(String filePath) throws Exception {
        List<File> files = FileUtil.getListFiles(filePath);
        for (File file : files) {
            executor.execute(() -> {
                long start = System.currentTimeMillis();
                try (LineIterator it = FileUtils.lineIterator(file, "UTF-8")) {
                    while (it.hasNext()) {
                        String line = it.nextLine();

                        if (line.indexOf("\"{\"") > 0 && line.indexOf("\"}\"") > 0) {
                            String jsonValue = line.substring(line.indexOf("\"{\""), line.indexOf("\"}\"") + 3);
                            //jsonValue = jsonValue.replace("\"{\"", "{").replace("\"}\"", "}").replaceAll("\\\\\"","");
                            String s = StringEscapeUtils.escapeJson(jsonValue);
                            String s1 = s.replaceAll("\\\\\"\\\\\"", "\\\"");
                            String s2 = s1.replaceAll("\\\\\"", "");
                            //System.out.println(s2);
                            Map<String, Object> eventValueMap = null;
                            try {
                                eventValueMap = JacksonUtil.readValue(s2, Map.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (eventValueMap != null) {
                                String eventName = line.substring(0, line.indexOf("\"{\"") - 1);
                                String valueNeeded = AppEventEnums.valueOf(eventName).getEventValueFromEventValue(eventValueMap);
                                String jsonAfter = line.substring(line.indexOf("\"}\"") + 4);
                                //System.out.println(value.substring(0,value.indexOf("\"{\"")-1));
                                //System.out.println(value.substring(value.indexOf("\"}\"") +4));
                                String[] deviceIdAndDateAndSite = jsonAfter.split(",");
                                String deviceId = deviceIdAndDateAndSite[0];
                                String userId = deviceIdAndDateAndSite[1];
                                //System.out.println(deviceId);
                                String date = deviceIdAndDateAndSite[2];
                                String site = SiteUtil.getAppSite(deviceIdAndDateAndSite[3]);
                                String platform = deviceIdAndDateAndSite[4];
                                if (StringUtils.isNotEmpty(valueNeeded) && "zaful".equals(site)) {

                                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                    DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                                    Long timestamp = null;
                                    if (date.contains("/")) {
                                        try {
                                            timestamp = dateFormat.parse(date).getTime();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (date.contains("-")) {
                                        try {
                                            timestamp = dateFormat2.parse(date).getTime();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (StringUtils.isNotEmpty(valueNeeded) && eventValueMap != null && eventValueMap.size() > 0 && timestamp != null) {
                                        for (String s123 : valueNeeded.split(",")) {
                                            Map<String, Object> eventDataRow = new HashMap<>();
                                            eventDataRow.put("event_name", eventName);
                                            eventDataRow.put("event_value", s123);
                                            eventDataRow.put("user_id", userId);
                                            eventDataRow.put("device_id", deviceId);
                                            eventDataRow.put("platform", platform);
                                            eventDataRow.put("site", site);
                                            eventDataRow.put(NginxLogConvertUtil.TIMESTAMP_KEY, timestamp);
                                            BulkRequest bulkRequest = new BulkRequest();
                                            //out.collect(eventDataRow);
                                            Index index = new Index.Builder(eventDataRow).index("dy-realtime-user-event-sequence").type("log").setParameter(Parameters.ROUTING, deviceId).build();
                                            jestClient.execute(index);
                                            /*jestClient.executeAsync(index, new JestResultHandler<DocumentResult>() {
                                                @Override
                                                public void completed(DocumentResult result) {

                                                }

                                                @Override
                                                public void failed(Exception ex) {
                                                    logger.error("send to es error", ex);
                                                }
                                            });*/
                                        }
                                    }

                                }
                            }

                        }


                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
                logger.info("文件 {} 处理完成，耗时: {}", file.getName(), System.currentTimeMillis() - start);
            });
            logger.info("文件 {} 提交成功 ", file.getName());
        }
        return "success";
    }
}
