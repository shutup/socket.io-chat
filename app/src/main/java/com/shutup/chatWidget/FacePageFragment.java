package com.shutup.chatWidget;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;


import java.util.ArrayList;
import java.util.List;

import com.shutup.globalrandomchat.R;

//@SuppressLint("InflateParams")
public class FacePageFragment extends Fragment {

    public static final String ARG_POSITION = "position";
    public static final String ARG_FACE_DATA = "ARG_FACE_DATA";
    Activity activity;
    List<View> faceGridViewList;
    List<ImageView> pointViews;
    private int position;
    private OnOperationListener onOperationListener;
    private List<String> data;
    private ViewPager faceViewPager;
    private LinearLayout pagePointLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        position = getArguments().getInt(ARG_POSITION);
        data = getArguments().getStringArrayList(ARG_FACE_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        faceGridViewList = new ArrayList<View>();
        pointViews = new ArrayList<ImageView>();

        View rootView = inflater.inflate(R.layout.face_fragment, null);
        faceViewPager = (ViewPager) rootView.findViewById(R.id.faceViewPager);
        pagePointLayout = (LinearLayout) rootView.findViewById(R.id.pagePointLayout);

        for (int x = 0; x < (data.size() % 12 == 0 ? data.size() / 12 : (data.size() / 12) + 1); x++) {
            GridView view = new GridView(activity);
            FaceAdapter faceAdapter = new FaceAdapter(activity,
                    data.subList(x * 12,
                            ((x + 1) * 12) > data.size() ? data.size() : ((x + 1) * 12)));
            view.setAdapter(faceAdapter);

            view.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (onOperationListener != null) {
                        onOperationListener.selectedFace(data.get(position));
                    }
                }
            });
            view.setNumColumns(4);

            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);

            faceGridViewList.add(view);

            ImageView imageView = new ImageView(activity);
            imageView.setBackgroundResource(R.drawable.point_normal);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            pagePointLayout.addView(imageView, layoutParams);
            if (x == 0) {
                imageView.setBackgroundResource(R.drawable.point_selected);
            }
            pointViews.add(imageView);
        }

        PagerAdapter facePagerAdapter = new FacePagerAdapter(faceGridViewList);
        faceViewPager.setAdapter(facePagerAdapter);
        faceViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {

                for (int i = 0; i < pointViews.size(); i++) {
                    if (index == i) {
                        pointViews.get(i).setBackgroundResource(R.drawable.point_selected);
                    } else {
                        pointViews.get(i).setBackgroundResource(R.drawable.point_normal);
                    }
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        return rootView;

    }

    public OnOperationListener getOnOperationListener() {
        return onOperationListener;
    }

    public void setOnOperationListener(OnOperationListener onOperationListener) {
        this.onOperationListener = onOperationListener;
    }

    public class FacePagerAdapter extends PagerAdapter {
        private List<View> gridViewList;

        public FacePagerAdapter(List<View> gridViewList) {
            this.gridViewList = gridViewList;
        }

        @Override
        public int getCount() {
            return gridViewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
            arg0.removeView(gridViewList.get(arg1));
        }

        @Override
        public Object instantiateItem(ViewGroup arg0, int arg1) {
            arg0.addView(gridViewList.get(arg1));
            return gridViewList.get(arg1);
        }

    }

}