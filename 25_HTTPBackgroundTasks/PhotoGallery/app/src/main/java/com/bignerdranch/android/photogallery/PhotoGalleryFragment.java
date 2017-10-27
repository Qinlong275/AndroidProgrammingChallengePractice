package com.bignerdranch.android.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";
    private RecyclerView mPhotoRecyclerView;
    private TextView mCurrentPageView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private GridLayoutManager mGridLayoutManager;

    private int current_page = 1;
    private boolean amLoading=false;
    private int mItemsPerPage;
    private int maxPage;
    private int maxItems;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute(current_page);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mCurrentPageView = (TextView) v.findViewById(R.id.currentPageView);
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        mGridLayoutManager=new GridLayoutManager(getActivity(), 3);
        mPhotoRecyclerView.setLayoutManager(mGridLayoutManager);

        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if ( dy > 0 || dy < 0) {
                    // Scrolling up or down
                    if ( !(amLoading) &&             // Not already processing a page fetch
                            (dy > 0 ) &&                 // Scrolling down
                            (current_page < maxPage) &&   // Haven't hit the bottom yet
                            mGridLayoutManager.findLastVisibleItemPosition() >= (mItems.size()-1) ) {
                        // We scrolled to the last row of the previously fetched set
                        //
                        // Go fetch more.
                        Log.d(TAG, "Fetching more items");
                        amLoading = true;
                        current_page++;
                        new FetchItemsTask().execute(current_page); // Also updates current page view
                    } else {
                        // Make sure our page value is correct
                        int firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();
                        int calcPage = 0;
                        if ( firstVisibleItem < mItemsPerPage) {
                            calcPage = 1;
                        } else {
                            calcPage = (firstVisibleItem / mItemsPerPage) +
                                    (firstVisibleItem % mItemsPerPage == 0 ? 0 : 1);
                        }
                        if ( calcPage != current_page ) {
                            current_page = calcPage;
                        }
                        setCurrentPageView(firstVisibleItem);
                    }
                }
            }
        });
        setupAdapter();

        return v;
    }

    private void setCurrentPageView() {
        setCurrentPageView(-1);
    }
    private void setCurrentPageView(int firstVisibleItem) {
        if ( firstVisibleItem == -1 ) {
            firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();
        }
        mCurrentPageView.setText("Current Fetched Page: " + current_page +
                " of " + ((maxPage==0) ? "<unknown>": maxPage) +
                ", " + ((mItemsPerPage==0)?"<unknown>":mItemsPerPage) + " items per page" +
                ", " + ((maxItems==0)?"<unknown>":maxItems) + " total items" +
                ", you've scrolled past: " + (firstVisibleItem <= 0 ? 0: firstVisibleItem) +
                " items.");
    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;

        public PhotoHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem item) {
            mTitleTextView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            photoHolder.bindGalleryItem(galleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Integer,Void,List<GalleryItem>> {

        private static final int COLUMNS_SIZE=200;
        private int mGridColumns;

        @Override
        protected List<GalleryItem> doInBackground(Integer... params) {
            FlickrFetchr mFetcher=new FlickrFetchr();
            mFetcher.fetchItems(params[0]);
            maxPage=mFetcher.getMaxPages();
            mItemsPerPage=mFetcher.getItemsPerPage();
            maxItems=mFetcher.getTotalItems();

            return mFetcher.fetchItems(params[0]);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            if ( mItems.size() == 0) {
                mItems.addAll(items);
                setupAdapter();
                setCurrentPageView();
            } else {
                final int oldSize = mItems.size();
                mItems.addAll(items);
                mPhotoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mPhotoRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        // Scroll to first row of newly added set
                        mPhotoRecyclerView.smoothScrollToPosition(oldSize);
                        // Adjust the columns to fit based on width of RecyclerView
                        int width = mPhotoRecyclerView.getWidth();
                        mGridColumns = width / COLUMNS_SIZE;
                        mGridLayoutManager = new GridLayoutManager(getActivity(),mGridColumns);
                        mPhotoRecyclerView.setLayoutManager(mGridLayoutManager);
                        setCurrentPageView();
                        amLoading = false;
                    }
                });
                mPhotoRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }
}
