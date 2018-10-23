package com.vito.ad.channels.oneway.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vito.ad.base.task.ADDownloadTask;
import com.vito.ad.base.task.ADInfoTask;
import com.vito.ad.channels.oneway.OneWayAdContent;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.ADDownloadTaskManager;
import com.vito.ad.views.ILandView;
import com.vito.utils.Log;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import vito.com.vitoadlibs.R;

public class ImplLandView2 extends ILandView{
//    private LinearLayout layout;
    private MZBannerView mMZBanner;
    //private FrameLayout coverLayout;
    private ImageView landPagebg;
    private BlurTransformation blurTransformation = new BlurTransformation( 14, 3);
    @Override
    public void buildLandView(final Context context, final ADInfoTask mADInfoTask) {
        OneWayAdContent oneWayAdContent = mADInfoTask.getADObject(OneWayAdContent.class);
        final List<String> imageUrl = oneWayAdContent.getImgUrls();
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout page_layout = (LinearLayout) inflater.inflate(R.layout.landing_page2, null, false);

        mMZBanner = page_layout.findViewById(R.id.banner);

        // 设置数据
        mMZBanner.setPages(imageUrl, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
        mMZBanner.start();
        coverLayout = (FrameLayout) inflater.inflate(R.layout.landing_cover, null, false);
        FrameLayout frameLayout = coverLayout.findViewById(R.id.landing_page_space);
        frameLayout.addView(page_layout, mMZBanner.getLayoutParams());

        Button cloaeBtn = coverLayout.findViewById(R.id.close_ad);
        cloaeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ADTEST", "click close");
                onClose(mADInfoTask);
            }
        });
        Button smalDownloadBtn = coverLayout.findViewById(R.id.small_download);
        smalDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ADDownloadTask ADDownloadTask = ADDownloadTaskManager.getInstance().buildDownloadTaskByADTask(mADInfoTask);
                ADDownloadTaskManager.getInstance().pushTask(ADDownloadTask);
                AdTaskManager.getInstance().getIAdBaseInterface(mADInfoTask).onClick();
                Log.e("ADTEST", "click small download");
                onClose(mADInfoTask);
            }
        });
        Button downloadBtn = coverLayout.findViewById(R.id.download);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ADDownloadTask currentADDownloadTask = ADDownloadTaskManager.getInstance().getDownloadTaskByADId(mADInfoTask.getId());
                if (currentADDownloadTask !=null)
                    currentADDownloadTask.setApkDownload(true);
                ADDownloadTask ADDownloadTask = ADDownloadTaskManager.getInstance().buildDownloadTaskByADTask(mADInfoTask);
                AdTaskManager.getInstance().getIAdBaseInterface(mADInfoTask).onClick();
                ADDownloadTaskManager.getInstance().pushTask(ADDownloadTask);
                Log.e("ADTEST", "click download");
                onClose(mADInfoTask);
            }
        });
        TextView appname = coverLayout.findViewById(R.id.ad_appName);
        appname.setText(oneWayAdContent.getAppName());
        RatingBar ratingBar = coverLayout.findViewById(R.id.mRatingBar);
        ratingBar.setRating(oneWayAdContent.getRating());
        ImageView appIcon = coverLayout.findViewById(R.id.ad_appIcon);
        Glide.with(context.getApplicationContext())
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(1000000)
                                .centerCrop())
                .load(oneWayAdContent.getAppIcon())
                .into(appIcon);
        landPagebg = coverLayout.findViewById(R.id.landing_page_bg);
        mMZBanner.addPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Glide.with(context.getApplicationContext())
                        .setDefaultRequestOptions(
                                new RequestOptions()
                                        .frame(1000000)
                                        .centerCrop()
                                        .bitmapTransform(blurTransformation))
                        .load(imageUrl.get(position))
                        .into(landPagebg);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public static class BannerViewHolder implements MZViewHolder<String> {
        private ImageView mImageView;
        @Override
        public View createView(Context context) {
            // 返回页面布局
            View view = LayoutInflater.from(context).inflate(R.layout.landing_page,null);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
            return view;
        }

        @Override
        public void onBind(Context context, int i, String imageUrl) {
            Glide.with(context.getApplicationContext())
                    .setDefaultRequestOptions(
                            new RequestOptions()
                                    .frame(1000000))
                    .load(imageUrl)
                    .into(mImageView);
        }

    }

    @Override
    public void onClose(ADInfoTask adInfoTask) {
        if (mMZBanner!=null) {
            mMZBanner.pause();
            mMZBanner = null;
        }
        super.onClose(adInfoTask);
    }
}
