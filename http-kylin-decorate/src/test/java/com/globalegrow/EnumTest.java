package com.globalegrow;

public enum EnumTest {
    test{
        @Override
        void what() {
            this.handle();
        }
    }
    ;
    abstract void what();
    public void handle() {
        // ...
    }
}
