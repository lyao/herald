package org.church.volyn.media;

import org.church.volyn.App;
import org.church.volyn.downloadHelper.DiskCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 22.09.2015.
 */
public class MediaManager {
    static private MediaManager sInstance;
    static private List<MediaContainer> mMediaContainers;
    static private List<MediaContainer> mDefaultMediaContainers;
    static private int sCapacity;

    public static final String MEDIA_FILE = "programs.json";

    enum Medias {
        MEDIA1("tv_grace_font"),
        MEDIA2("tv_volyn_bells"),
        MEDIA3("radio_orthodox_word"),
        MEDIA4("radio_spiritual_land_mark");

        private final String mediaValue;

        Medias(String mediaValue) {
            this.mediaValue = mediaValue;
        }

        public String mediaValue() {
            return mediaValue;
        }

        @Override
        public String toString() {
            return mediaValue;
        }
    }

    final static private int DEFAULT_CAPACITY = 4;

    private MediaManager() {
        mMediaContainers = new ArrayList<>();
    }

    public static void init(int capacity) {
        sCapacity = capacity;
        if (mMediaContainers == null) {
            //mMediaContainers = new ArrayList<>(sCapacity);
            mMediaContainers = new ArrayList<>();
        } else {
            mMediaContainers.clear();
        }
    }

    public static MediaManager getInstance() {
        if (sInstance == null)
            sInstance = new MediaManager();
        return sInstance;
    }

    public void addMediaContainer(MediaContainer mediaContainer) {
        mMediaContainers.add(mediaContainer.getId(), mediaContainer);
    }

    public void addMedia(MediaElement mediaElement) {
        mMediaContainers.get(mediaElement.getCatId() - 1).addMediaElement(mediaElement);
    }

    public List<MediaContainer> getMediaContainers() {
        return mMediaContainers;
    }

    public List<MediaContainer> getMediaContainersFromFile() {
        String jsonText = DiskCache.getInstance(App.getContext()).readJsonFromFile(MEDIA_FILE);
        MediaParser.parse(jsonText);
        return mMediaContainers;
    }

    public List<MediaContainer> getDefaultMediaContainers() {

        if (mDefaultMediaContainers == null) {
            mDefaultMediaContainers = new ArrayList<>(DEFAULT_CAPACITY);
        }
        if (mDefaultMediaContainers.size() == 0) {
//            mDefaultMediaContainers = new ArrayList<>(DEFAULT_CAPACITY);
            for (int i = 0; i < DEFAULT_CAPACITY; i++) {
                MediaContainer mc = new MediaContainer(i + 1, Medias.values()[i].mediaValue);
                mc.setDrawableId(Medias.values()[i].mediaValue);
                mc.setType(MediaContainer.TYPE_AUDIO);
                mDefaultMediaContainers.add(mc);
            }
        }

        return mDefaultMediaContainers;
    }

}
