package com.xcbeyond.springcloud.eureka.rest.controller;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Pair;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.cluster.PeerEurekaNode;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl;
import com.netflix.eureka.resources.StatusResource;
import com.netflix.eureka.util.StatusInfo;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

/**
 * Eureka RestFull 接口。</br>
 * 重构org.springframework.cloud.netflix.eureka.server.EurekaController.java
 * 获取注册中心服务注册实例、状态等信息。
 * @Auther: xcbeyond
 * @Date: 2018/11/22 00:46
 */
@RestController
@RequestMapping("/eurekaRest")
public class EurekaRestController {
    private String dashboardPath = "";
    private ApplicationInfoManager applicationInfoManager;

    public EurekaRestController(ApplicationInfoManager applicationInfoManager) {
        this.applicationInfoManager = applicationInfoManager;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public String status() {
        Map<String, Object> model = Maps.newHashMap();;
        this.populateBase(model);
        this.populateApps(model);

        StatusInfo statusInfo = null;
        try {
            statusInfo = (new StatusResource()).getStatusInfo();
            statusInfo.isHealthy();//解决NullPointerException
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                ReflectUtil.setFieldValue(statusInfo, "isHeathly", true);
            } else {
                statusInfo = StatusInfo.Builder.newBuilder().isHealthy(false).build();
            }
        }

        model.put("statusInfo", statusInfo);
        this.populateInstanceInfo(model, statusInfo);
        this.filterReplicas(model, statusInfo);

        return JSON.toJSONString(model);
    }

    @RequestMapping(value = "/lastn", method = RequestMethod.GET)
    public String lastn(Map<String, Object> model) {
        populateBase(model);
        PeerAwareInstanceRegistryImpl registry = (PeerAwareInstanceRegistryImpl) getRegistry();
        ArrayList<Map<String, Object>> lastNCanceled = new ArrayList<>();
        List<Pair<Long, String>> list = registry.getLastNCanceledInstances();
        for (Pair<Long, String> entry : list) {
            lastNCanceled.add(registeredInstance(entry.second(), entry.first()));
        }
        model.put("lastNCanceled", lastNCanceled);
        list = registry.getLastNRegisteredInstances();
        ArrayList<Map<String, Object>> lastNRegistered = new ArrayList<>();
        for (Pair<Long, String> entry : list) {
            lastNRegistered.add(registeredInstance(entry.second(), entry.first()));
        }
        model.put("lastNRegistered", lastNRegistered);
        return JSON.toJSONString(model);
    }

    private Map<String, Object> registeredInstance(String id, long date) {
        HashMap<String, Object> map = new HashMap();
        map.put("id", id);
        map.put("date", new Date(date));
        return map;
    }

    protected void populateBase(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("basePath", "/");
//        model.put("dashboardPath", this.dashboardPath.equals("/") ? "" : this.dashboardPath);
        this.populateHeader(model);
        this.populateNavbar(model);
    }

    private void populateHeader(Map<String, Object> model) {
        model.put("currentTime", StatusResource.getCurrentTimeAsString());
        model.put("upTime", StatusInfo.getUpTime());
        model.put("environment", ConfigurationManager.getDeploymentContext()
                .getDeploymentEnvironment());
        model.put("datacenter", ConfigurationManager.getDeploymentContext()
                .getDeploymentDatacenter());
        PeerAwareInstanceRegistry registry = getRegistry();
        model.put("registry", registry);
        model.put("isBelowRenewThresold", registry.isBelowRenewThresold() == 1);
        DataCenterInfo info = applicationInfoManager.getInfo().getDataCenterInfo();
        if (info.getName() == DataCenterInfo.Name.Amazon) {
            AmazonInfo amazonInfo = (AmazonInfo) info;
            model.put("amazonInfo", amazonInfo);
            model.put("amiId", amazonInfo.get(AmazonInfo.MetaDataKey.amiId));
            model.put("availabilityZone",
                    amazonInfo.get(AmazonInfo.MetaDataKey.availabilityZone));
            model.put("instanceId", amazonInfo.get(AmazonInfo.MetaDataKey.instanceId));
        }
    }

    private PeerAwareInstanceRegistry getRegistry() {
        return this.getServerContext().getRegistry();
    }

    private EurekaServerContext getServerContext() {
        return EurekaServerContextHolder.getInstance().getServerContext();
    }

    private void populateNavbar(Map<String, Object> model) {
        Map<String, String> replicas = new LinkedHashMap<>();
        List<PeerEurekaNode> list = getServerContext().getPeerEurekaNodes().getPeerNodesView();
        for (PeerEurekaNode node : list) {
            try {
                URI uri = new URI(node.getServiceUrl());
                String href = scrubBasicAuth(node.getServiceUrl());
                replicas.put(uri.getHost(), href);
            }
            catch (Exception ex) {
                // ignore?
            }
        }
        model.put("replicas", replicas.entrySet());
    }

    private void populateApps(Map<String, Object> model) {
        List<Application> sortedApplications = getRegistry().getSortedApplications();
        ArrayList<Map<String, Object>> apps = new ArrayList<>();
        for (Application app : sortedApplications) {
            LinkedHashMap<String, Object> appData = new LinkedHashMap<>();
            apps.add(appData);
            appData.put("name", app.getName());
            Map<String, Integer> amiCounts = new HashMap<>();
            Map<InstanceInfo.InstanceStatus, List<Pair<String, String>>> instancesByStatus = new HashMap<>();
            Map<String, Integer> zoneCounts = new HashMap<>();
            for (InstanceInfo info : app.getInstances()) {
                String id = info.getId();
                String url = info.getStatusPageUrl();
                InstanceInfo.InstanceStatus status = info.getStatus();
                String ami = "n/a";
                String zone = "";
                if (info.getDataCenterInfo().getName() == DataCenterInfo.Name.Amazon) {
                    AmazonInfo dcInfo = (AmazonInfo) info.getDataCenterInfo();
                    ami = dcInfo.get(AmazonInfo.MetaDataKey.amiId);
                    zone = dcInfo.get(AmazonInfo.MetaDataKey.availabilityZone);
                }
                Integer count = amiCounts.get(ami);
                if (count != null) {
                    amiCounts.put(ami, count + 1);
                }
                else {
                    amiCounts.put(ami, 1);
                }
                count = zoneCounts.get(zone);
                if (count != null) {
                    zoneCounts.put(zone, count + 1);
                }
                else {
                    zoneCounts.put(zone, 1);
                }
                List<Pair<String, String>> list = instancesByStatus.get(status);
                if (list == null) {
                    list = new ArrayList<>();
                    instancesByStatus.put(status, list);
                }
                list.add(new Pair<>(id, url));
            }
            appData.put("amiCounts", amiCounts.entrySet());
            appData.put("zoneCounts", zoneCounts.entrySet());
            ArrayList<Map<String, Object>> instanceInfos = new ArrayList<>();
            appData.put("instanceInfos", instanceInfos);
            for (Iterator<Map.Entry<InstanceInfo.InstanceStatus, List<Pair<String, String>>>> iter = instancesByStatus
                    .entrySet().iterator(); iter.hasNext();) {
                Map.Entry<InstanceInfo.InstanceStatus, List<Pair<String, String>>> entry = iter
                        .next();
                List<Pair<String, String>> value = entry.getValue();
                InstanceInfo.InstanceStatus status = entry.getKey();
                LinkedHashMap<String, Object> instanceData = new LinkedHashMap<>();
                instanceInfos.add(instanceData);
                instanceData.put("status", entry.getKey());
                ArrayList<Map<String, Object>> instances = new ArrayList<>();
                instanceData.put("instances", instances);
                instanceData.put("isNotUp", status != InstanceInfo.InstanceStatus.UP);

                // TODO

                /*
                 * if(status != InstanceInfo.InstanceStatus.UP){
                 * buf.append("<font color=red size=+1><b>"); }
                 * buf.append("<b>").append(status
                 * .name()).append("</b> (").append(value.size()).append(") - ");
                 * if(status != InstanceInfo.InstanceStatus.UP){
                 * buf.append("</font></b>"); }
                 */

                for (Pair<String, String> p : value) {
                    LinkedHashMap<String, Object> instance = new LinkedHashMap<>();
                    instances.add(instance);
                    instance.put("id", p.first());
                    String url = p.second();
                    instance.put("url", url);
                    boolean isHref = url != null && url.startsWith("http");
                    instance.put("isHref", isHref);
                    /*
                     * String id = p.first(); String url = p.second(); if(url != null &&
                     * url.startsWith("http")){
                     * buf.append("<a href=\"").append(url).append("\">"); }else { url =
                     * null; } buf.append(id); if(url != null){ buf.append("</a>"); }
                     * buf.append(", ");
                     */
                }
            }
            // out.println("<td>" + buf.toString() + "</td></tr>");
        }
        model.put("apps", apps);
    }

    private void populateInstanceInfo(Map<String, Object> model, StatusInfo statusInfo) {
        InstanceInfo instanceInfo = statusInfo.getInstanceInfo();
        Map<String, String> instanceMap = new HashMap<>();
        instanceMap.put("ipAddr", instanceInfo.getIPAddr());
        instanceMap.put("status", instanceInfo.getStatus().toString());
        if (instanceInfo.getDataCenterInfo().getName() == DataCenterInfo.Name.Amazon) {
            AmazonInfo info = (AmazonInfo) instanceInfo.getDataCenterInfo();
            instanceMap.put("availability-zone",
                    info.get(AmazonInfo.MetaDataKey.availabilityZone));
            instanceMap.put("public-ipv4", info.get(AmazonInfo.MetaDataKey.publicIpv4));
            instanceMap.put("instance-id", info.get(AmazonInfo.MetaDataKey.instanceId));
            instanceMap.put("public-hostname",
                    info.get(AmazonInfo.MetaDataKey.publicHostname));
            instanceMap.put("ami-id", info.get(AmazonInfo.MetaDataKey.amiId));
            instanceMap.put("instance-type",
                    info.get(AmazonInfo.MetaDataKey.instanceType));
        }
        model.put("instanceInfo", instanceMap);
    }

    protected void filterReplicas(Map<String, Object> model, StatusInfo statusInfo) {
        Map<String, String> applicationStats = statusInfo.getApplicationStats();
        if(applicationStats.get("registered-replicas").contains("@")){
            applicationStats.put("registered-replicas", scrubBasicAuth(applicationStats.get("registered-replicas")));
        }
        if(applicationStats.get("unavailable-replicas").contains("@")){
            applicationStats.put("unavailable-replicas",scrubBasicAuth(applicationStats.get("unavailable-replicas")));
        }
        if(applicationStats.get("available-replicas").contains("@")){
            applicationStats.put("available-replicas",scrubBasicAuth(applicationStats.get("available-replicas")));
        }
        model.put("applicationStats", applicationStats);
    }

    private String scrubBasicAuth(String urlList){
        String[] urls=urlList.split(",");
        StringBuilder filteredUrls = new StringBuilder();
        for(String u : urls){
            if(u.contains("@")){
                filteredUrls.append(u.substring(0,u.indexOf("//")+2)).append(u.substring(u.indexOf("@")+1,u.length())).append(",");
            }else{
                filteredUrls.append(u).append(",");
            }
        }
        return filteredUrls.substring(0,filteredUrls.length()-1);
    }

}
