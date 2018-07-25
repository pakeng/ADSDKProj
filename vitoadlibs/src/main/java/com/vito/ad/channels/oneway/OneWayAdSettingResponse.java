package com.vito.ad.channels.oneway;

public class OneWayAdSettingResponse extends Response {

    private ContentData data ;


    public ContentData getData() {
        return data;
    }

    public void setData(ContentData data) {
        this.data = data;
    }


    public class ContentData {
        public String getAppToken() {
            return appToken;
        }

        public void setAppToken(String appToken) {
            this.appToken = appToken;
        }

        public boolean isTestMode() {
            return testMode;
        }

        public void setTestMode(boolean testMode) {
            this.testMode = testMode;
        }

        private String appToken ="";
        private boolean testMode = false;
    }
}
