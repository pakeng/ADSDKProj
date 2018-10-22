package com.vito.ad.channels.vlion.response;

public class Gdt_Download_Bean {
   private int ret; //	是	int	非 0 表示失败

   public boolean isSuccess(){
      return ret == 0;
   }

   public DownloadDetail getData() {
      return data;
   }

   public void setData(DownloadDetail data) {
      this.data = data;
   }

   private DownloadDetail data; //	是	object

}
