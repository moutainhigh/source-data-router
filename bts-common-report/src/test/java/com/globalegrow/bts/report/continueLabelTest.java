package com.globalegrow.bts.report;

/**
 * @Auther: joker
 * @Date: 2019/6/28 11:22
 * @Description:
 */
public class continueLabelTest {
    public static void main(String[] args){
        int i = 0;
        outer:
        for(;true;){
            inner:
            for(;i<10;i++){
                System.out.println("i="+i);
                /**在抵达for循环的末尾之前，递增表达式会执行*/
                if(i==2){
                    System.out.println("continue");
                    continue;
                }
                /**break会中断for循环，而且在抵达for循环的末尾之前，递增表达式不会执行。由于
                 * break跳过了增量表达式，所以在此处直接对i进行递增操作*/
                if(i==3){
                    System.out.println("break");
                    i++;
                    break;
                }
                /**continue outer语句会跳到循环顶部，而且也会跳过inner标签下的for循环的递增，
                 * 所以这里也对i直接递增*/
                if(i==7){
                    System.out.println("continue outer");
                    i++;
                    continue outer;
                }
                if(i==8){
                    System.out.println("break outer");
                    break outer;
                }
                /**在抵达inner标签下的for循环末尾之前，inner下的for循环的递增表达式会执行*/
                for(int k = 0;k< 5;k++){
                    if(k==3){
                        System.out.println("continue inner");
                        continue inner;
                    }
                }
            }
        }
    }
}

