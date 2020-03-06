package cgeo.geocaching;

import cgeo.geocaching.activity.Progress;
import cgeo.geocaching.apps.navi.NavigationAppFactory;
import cgeo.geocaching.compatibility.Compatibility;
import cgeo.geocaching.list.StoredList;
import cgeo.geocaching.models.Geocache;
import cgeo.geocaching.network.Network;
import cgeo.geocaching.persistence.entities.CacheList;
import cgeo.geocaching.persistence.repositories.GeocacheRepository;
import cgeo.geocaching.settings.Settings;
import cgeo.geocaching.speech.SpeechService;
import cgeo.geocaching.storage.DataStore;
import cgeo.geocaching.ui.CacheDetailsCreator;
import cgeo.geocaching.ui.WeakReferenceHandler;
import cgeo.geocaching.utils.AndroidRxUtils;
import cgeo.geocaching.utils.DisposableHandler;
import cgeo.geocaching.utils.Log;
import cgeo.geocaching.utils.TextUtils;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.StringUtils;

public class CachePopupFragment extends AbstractDialogFragmentWithProximityNotification {
    private final Progress progress = new Progress();

    protected LiveData<cgeo.geocaching.persistence.entities.Geocache> geocache;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            if (getArguments().getString(GEOCODE_ARG) != null) {
                geocache = cacheDetailsViewModel.getGeocacheByGeocode(getArguments().getString(GEOCODE_ARG), cgeo.geocaching.persistence.entities.Geocache.DetailLevel.POPUP, false);
                geocache.observe(this, this::updateCacheData);
                cacheDetailsViewModel.getDownloadStatus(getArguments().getString(GEOCODE_ARG)).observe(this, status -> {
                    switch (status.status) {
                        case LOADING:
                            progress.setMessage(status.message);
                            break;
                        case SUCCESS:
                        case ERROR:
                            progress.dismiss();
                    }
                });

                cacheDetailsViewModel.getLists(getArguments().getString(GEOCODE_ARG)).observe(this, this::updateCacheLists);
            }
        }
    }

    private void updateCacheLists(final List<CacheList> lists) {
        final View view = getView();
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        for (final CacheList list : lists) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            final int start = builder.length();
            builder.append(list.name);
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(final View widget) {
                    Settings.setLastDisplayedList((int) list.listId);
                    CacheListActivity.startActivityOffline(view.getContext());
                }
            }, start, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        builder.insert(0, res.getString(R.string.list_list_headline) + " ");
        final TextView offlineLists = view.findViewById(R.id.offline_lists);
        offlineLists.setText(builder);
        offlineLists.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void updateCacheData(final cgeo.geocaching.persistence.entities.Geocache cache) {
        if (null != proximityNotification) {
            proximityNotification.setReferencePoint(cache.getCoords());
            proximityNotification.setTextNotifications(getContext());
        }

        if (StringUtils.isNotBlank(cache.name)) {
            setTitle(TextUtils.coloredCacheText(cache, cache.name));
        } else {
            setTitle(geocode);
        }

        final View view = getView();
        assert view != null;
        final TextView titleView = view.findViewById(R.id.actionbar_title);
        titleView.setCompoundDrawablesWithIntrinsicBounds(Compatibility.getDrawable(getResources(), cache.cacheType.markerId), null, null, null);

        final LinearLayout layout = view.findViewById(R.id.details_list);
        details = new CacheDetailsCreator(getActivity(), layout);

        addCacheDetails(cache);
    }

    public static DialogFragment newInstance(final String geocode) {

        final Bundle args = new Bundle();
        args.putString(GEOCODE_ARG, geocode);

        final DialogFragment f = new CachePopupFragment();
        f.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        f.setArguments(args);

        return f;
    }

    // TODO use full detail download from repository
    private static class StoreCacheHandler extends DisposableHandler {
        private final int progressMessage;
        private final WeakReference<CachePopupFragment> popupRef;

        StoreCacheHandler(final CachePopupFragment popup, final int progressMessage) {
            this.progressMessage = progressMessage;
            popupRef = new WeakReference<>(popup);
        }


        private void updateStatusMsg(final String msg) {
            final CachePopupFragment popup = popupRef.get();
            if (popup == null || !popup.isAdded()) {
                return;
            }
            popup.progress.setMessage(popup.getString(progressMessage)
                    + "\n\n"
                    + msg);
        }

        @Override
        protected void handleRegularMessage(final Message message) {

        }
    }

    private static class DropCacheHandler extends WeakReferenceHandler<CachePopupFragment> {

        DropCacheHandler(final CachePopupFragment popup) {
            super(popup);
        }

        @Override
        public void handleMessage(final Message msg) {
            final CachePopupFragment popup = getReference();
            if (popup == null) {
                return;
            }
            popup.getActivity().finish();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.popup, container, false);
        initCustomActionBar(v);
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }

        final int menuItem = item.getItemId();

        switch (menuItem) {
            case R.id.menu_delete:
                new DropCacheClickListener().onClick(getView());
                return true;
            case R.id.menu_tts_toggle:
                if (geocache.getValue() != null) {
                    SpeechService.toggleService(getActivity(), geocache.getValue().getCoords());
                } else {
                    // TODO handle error, inform user
                }
                return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        SpeechService.stopService(getActivity());
        super.onDestroy();
    }

    private class StoreCacheClickListener implements View.OnClickListener, View.OnLongClickListener {
        @Override
        public void onClick(final View arg0) {
            selectListsAndStore(false);
        }

        @Override
        public boolean onLongClick(final View v) {
            selectListsAndStore(true);
            return true;
        }

        private void selectListsAndStore(final boolean fastStoreOnLastSelection) {
            if (progress.isShowing()) {
                showToast(res.getString(R.string.err_detail_still_working));
                return;
            }

            if (Settings.getChooseList() || cache.isOffline()) {
                // let user select list to store cache in
                new StoredList.UserInterface(getActivity()).promptForMultiListSelection(R.string.lists_title,
                        selectedListIds -> storeCacheOnLists(selectedListIds), true, cache.getLists(), fastStoreOnLastSelection);
            } else {
                storeCacheOnLists(Collections.singleton(StoredList.STANDARD_LIST_ID));
            }
        }

        private void storeCacheOnLists(final Set<Integer> listIds) {

            if (cache.isOffline()) {
                // cache already offline, just add to another list
                DataStore.saveLists(Collections.singletonList(cache), listIds);
                CacheDetailActivity.updateOfflineBox(getView(), cache, res,
                        new RefreshCacheClickListener(), new DropCacheClickListener(),
                        new StoreCacheClickListener(), new ShowHintClickListener(getView()), null, new StoreCacheClickListener());
                CacheDetailActivity.updateCacheLists(getView(), cache, res);
            } else {
                final StoreCacheHandler storeCacheHandler = new StoreCacheHandler(CachePopupFragment.this, R.string.cache_dialog_offline_save_message);
                final FragmentActivity activity = getActivity();
                progress.show(activity, res.getString(R.string.cache_dialog_offline_save_title), res.getString(R.string.cache_dialog_offline_save_message), true, storeCacheHandler.disposeMessage());
                AndroidRxUtils.andThenOnUi(Schedulers.io(), () -> cache.store(listIds, storeCacheHandler), () -> {
                    activity.supportInvalidateOptionsMenu();
                    final View view = getView();
                    if (view != null) {
                        CacheDetailActivity.updateOfflineBox(view, cache, res,
                                new RefreshCacheClickListener(), new DropCacheClickListener(),
                                new StoreCacheClickListener(), new ShowHintClickListener(view), null, new StoreCacheClickListener());
                        CacheDetailActivity.updateCacheLists(view, cache, res);
                    }
                });
            }

            final GeocacheRepository geocacheRepository = new GeocacheRepository(CgeoApplication.getInstance());
            for (Integer listId : listIds) {
                geocacheRepository.addGeocacheToList(cache.getGeocode(), listId);
            }
        }
    }

    private class RefreshCacheClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View arg0) {
            if (progress.isShowing()) {
                showToast(res.getString(R.string.err_detail_still_working));
                return;
            }

            if (!Network.isConnected()) {
                showToast(getString(R.string.err_server));
                return;
            }

            final StoreCacheHandler refreshCacheHandler = new StoreCacheHandler(CachePopupFragment.this, R.string.cache_dialog_offline_save_message);
            progress.show(getActivity(), res.getString(R.string.cache_dialog_refresh_title), res.getString(R.string.cache_dialog_refresh_message), true, refreshCacheHandler.disposeMessage());
            cache.refresh(refreshCacheHandler, AndroidRxUtils.networkScheduler);
        }
    }

    private class DropCacheClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View arg0) {
            if (progress.isShowing()) {
                showToast(res.getString(R.string.err_detail_still_working));
                return;
            }

            final DropCacheHandler dropCacheHandler = new DropCacheHandler(CachePopupFragment.this);
            progress.show(getActivity(), res.getString(R.string.cache_dialog_offline_drop_title), res.getString(R.string.cache_dialog_offline_drop_message), true, null);
            cache.drop(dropCacheHandler);
        }
    }

    private class ShowHintClickListener implements View.OnClickListener {
        private View anchorView;

        ShowHintClickListener (final View view) {
            anchorView = view;
        }

        @Override
        public void onClick(final View view) {
            final TextView offlineHintText = (TextView) anchorView.findViewById(R.id.offline_hint_text);
            final View offlineHintSeparator = anchorView.findViewById(R.id.offline_hint_separator);
            if (offlineHintText.getVisibility() == View.VISIBLE) {
                offlineHintText.setVisibility(View.GONE);
                offlineHintSeparator.setVisibility(View.GONE);
            } else {
                offlineHintText.setVisibility(View.VISIBLE);
                offlineHintSeparator.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void navigateTo() {
        if (geocache.getValue() != null) {
            NavigationAppFactory.startDefaultNavigationApplication(1, getActivity(), geocache.getValue().getCoords());
        }
    }

    @Override
    public void showNavigationMenu() {
        NavigationAppFactory.showNavigationMenu(getActivity(), geocache.getValue(), null, null, true, true);
    }


    /**
     * Tries to navigate to the {@link Geocache} of this activity.
     */
    @Override
    protected void startDefaultNavigation2() {
        if (geocache.getValue() == null || geocache.getValue().getCoords() == null) {
            showToast(res.getString(R.string.cache_coordinates_no));
            return;
        }
        // TODO use CacheNavigationApp (supporting parking Waypoints etc)
        NavigationAppFactory.startDefaultNavigationApplication(2, getActivity(), geocache.getValue().getCoords());
        getActivity().finish();
    }

    @Override
    protected TargetInfo getTargetInfo() {
        if (geocache.getValue() == null) {
            return null;
        }
        return new TargetInfo(geocache.getValue().getCoords(), geocache.getValue().geocode);
    }
}
