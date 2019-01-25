package com.globalegrow;

import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SentinelTest {

    @Test
    public void test() {
        List<FlowRule> flowRules = new ArrayList<>();
        FlowRuleManager.loadRules(flowRules);
    }

}
