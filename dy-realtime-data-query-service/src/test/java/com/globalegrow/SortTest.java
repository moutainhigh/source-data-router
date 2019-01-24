package com.globalegrow;

import com.globalegrow.dy.dto.UserActionData;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortTest {

    @Test
    public void sortTest() {
        List<UserActionData> userActionData = new ArrayList<>();

        UserActionData u1 = new UserActionData("5", 5L);
        UserActionData u2 = new UserActionData("9", 9L);
        UserActionData u3 = new UserActionData("10", 10L);
        UserActionData u4 = new UserActionData("1", 1L);
        UserActionData u5 = new UserActionData("3", 3L);

        userActionData.add(u1);
        userActionData.add(u2);
        userActionData.add(u3);
        userActionData.add(u4);
        userActionData.add(u5);

        System.out.println(userActionData);

        Collections.sort(userActionData);

        System.out.println(userActionData);

        System.out.println(userActionData.size());
        System.out.println(userActionData.subList(0, 5));
    }

}
