package com.u9porn.ui.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.constants.Constants;
import com.u9porn.data.network.Api;
import com.u9porn.data.prefs.AppPreferencesHelper;
import com.u9porn.ui.MvpActivity;
import com.u9porn.ui.google.GoogleRecaptchaVerifyActivity;
import com.u9porn.ui.porn9video.user.UserLoginActivity;
import com.u9porn.utils.DialogUtils;
import com.u9porn.utils.PlaybackEngine;
import com.u9porn.utils.SDCardUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.qqtheme.framework.picker.FilePicker;
import cn.qqtheme.framework.util.StorageUtils;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.HttpUrl;

/**
 * @author flymegoc
 */
public class SettingActivity extends MvpActivity<SettingView, SettingPresenter> implements View.OnClickListener, SettingView {

    private static final String TAG = SettingActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mine_list)
    QMUIGroupListView qmuiGroupListView;
    @BindView(R.id.bt_setting_exit_account)
    Button btSettingExitAccount;

    @Inject
    SettingPresenter settingPresenter;

    private AlertDialog testAlertDialog;
    private AlertDialog moveOldDirDownloadVideoToNewDirDiaog;
    private boolean isTestSuccess = false;
    private String testBaseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initToolBar(toolbar);
        initSettingSection();
        initListener();
        init();
    }

    @NonNull
    @Override
    public SettingPresenter createPresenter() {
        return settingPresenter;
    }

    private void init() {
        if (presenter.isUserLogin()) {
            btSettingExitAccount.setVisibility(View.VISIBLE);
        }
        testAlertDialog = DialogUtils.initLoadingDialog(context, "?????????????????????...");
        moveOldDirDownloadVideoToNewDirDiaog = DialogUtils.initLoadingDialog(context, "???????????????????????????...");
    }

    private void initListener() {
        btSettingExitAccount.setOnClickListener(this);
    }


    private void initSettingSection() {
        qmuiGroupListView.setSeparatorStyle(QMUIGroupListView.SEPARATOR_STYLE_NORMAL);
        QMUIGroupListView.Section tsec = QMUIGroupListView.newSection(this);
        //91pron??????
        QMUICommonListItemView addressItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.address_v9porn));
        addressItemWithChevron.setId(R.id.setting_item_9_porn_address);
        addressItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        String video91Address = presenter.getVideo9PornAddress();
        addressItemWithChevron.setDetailText(TextUtils.isEmpty(video91Address) ? "?????????" : video91Address);
        addressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //91????????????
        QMUICommonListItemView forumAddressItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.address_forum_9porn));
        forumAddressItemWithChevron.setId(R.id.setting_item_t6y_forum_address);
        forumAddressItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        String forum91Address = presenter.getForum9PornAddress();
        forumAddressItemWithChevron.setDetailText(TextUtils.isEmpty(forum91Address) ? "?????????" : forum91Address);
        forumAddressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //ZhuGuLi????????????
        QMUICommonListItemView pigAvAddressItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.address_pa));
        pigAvAddressItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        String pigAvAddress = presenter.getPavAddress();
        pigAvAddressItemWithChevron.setDetailText(TextUtils.isEmpty(pigAvAddress) ? "?????????" : pigAvAddress);
        pigAvAddressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //Axgle??????????????????
        QMUICommonListItemView axgleAddressItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.address_axgle));
        axgleAddressItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        String axgleAddress = presenter.getAxgleAddress();
        axgleAddressItemWithChevron.setDetailText(TextUtils.isEmpty(axgleAddress) ? "?????????API??????(??????????????????api???????????????)" : axgleAddress);
        axgleAddressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //????????????
        QMUICommonListItemView t6yAddressItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.address_t6y));
        t6yAddressItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        t6yAddressItemWithChevron.setId(R.id.setting_item_t6y_forum_address);
        t6yAddressItemWithChevron.setDetailText("???????????????????????????");
        t6yAddressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //???????????????
        QMUICommonListItemView kedouwoAddressItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.address_kedou));
        kedouwoAddressItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        kedouwoAddressItemWithChevron.setId(R.id.setting_item_kedou_address);
        String kedouwoAddress = presenter.getKeDouWoAddress();
        kedouwoAddressItemWithChevron.setDetailText(TextUtils.isEmpty(kedouwoAddress) ? "?????????" : kedouwoAddress);
        kedouwoAddressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        tsec.addItemView(addressItemWithChevron, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressSettingDialog((QMUICommonListItemView) v, AppPreferencesHelper.KEY_SP_PORN_91_VIDEO_ADDRESS);
            }
        });
        tsec.addItemView(forumAddressItemWithChevron, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressSettingDialog((QMUICommonListItemView) v, AppPreferencesHelper.KEY_SP_FORUM_91_PORN_ADDRESS);
            }
        });
        tsec.addItemView(pigAvAddressItemWithChevron, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressSettingDialog((QMUICommonListItemView) v, AppPreferencesHelper.KEY_SP_PIG_AV_ADDRESS);
            }
        });
        tsec.addItemView(axgleAddressItemWithChevron, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressSettingDialog((QMUICommonListItemView) v, AppPreferencesHelper.KEY_SP_AXGLE_ADDRESS);
            }
        });
        tsec.addItemView(t6yAddressItemWithChevron, this);
        tsec.addItemView(kedouwoAddressItemWithChevron, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressSettingDialog((QMUICommonListItemView) v, AppPreferencesHelper.KEY_SP_KE_DOU_WO_ADDRESS);
            }
        });
        tsec.addTo(qmuiGroupListView);

        //????????????
        QMUICommonListItemView playEngineItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.playback_engine));
        playEngineItemWithChevron.setId(R.id.setting_item_player_engine_choice);
        playEngineItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        final int checkedIndex = presenter.getPlaybackEngine();
        playEngineItemWithChevron.setDetailText(PlaybackEngine.PLAY_ENGINE_ITEMS[checkedIndex]);
        playEngineItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //?????????????????????
        final QMUICommonListItemView customDownloadPathItemWithChevron = qmuiGroupListView.createItemView("??????????????????????????????");
        customDownloadPathItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        String customDirPath = presenter.getCustomDownloadVideoDirPath();
        if (SDCardUtils.DOWNLOAD_VIDEO_PATH.equalsIgnoreCase(customDirPath)) {
            customDownloadPathItemWithChevron.setDetailText("??????????????????????????????????????????????????????");
        } else {
            customDownloadPathItemWithChevron.setDetailText(customDirPath);
        }

        customDownloadPathItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);


        QMUIGroupListView.newSection(this)
                .addItemView(playEngineItemWithChevron, this)
                .addItemView(customDownloadPathItemWithChevron, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectDownloadVideoDir(customDownloadPathItemWithChevron);
                    }
                })
                .addTo(qmuiGroupListView);


        QMUIGroupListView.Section sec = QMUIGroupListView.newSection(this);

        //??????????????????????????????
        boolean isForbidden = presenter.isForbiddenAutoReleaseMemory();
        QMUICommonListItemView itemWithSwitchForbidden = qmuiGroupListView.createItemView("??????????????????????????????");
        itemWithSwitchForbidden.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        itemWithSwitchForbidden.getSwitch().setChecked(isForbidden);
        itemWithSwitchForbidden.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.setForbiddenAutoReleaseMemory(isChecked);
                if (isChecked) {
                    showForbiddenReleaseMemoryTipInfoDialog();
                }
            }
        });

        //???Wi-Fi?????????????????????
        boolean isDownloadNeedWifi = presenter.isDownloadVideoNeedWifi();
        QMUICommonListItemView itemWithSwitch = qmuiGroupListView.createItemView("???Wi-Fi?????????????????????");
        itemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        itemWithSwitch.getSwitch().setChecked(!isDownloadNeedWifi);
        itemWithSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.setDownloadVideoNeedWifi(!isChecked);
            }
        });

        //??????91??????????????????
        boolean isOpenSkipPage = presenter.isOpenSkipPage();
        QMUICommonListItemView openSkipPageItemWithSwitch = qmuiGroupListView.createItemView("??????9*PORN??????????????????");
        openSkipPageItemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        openSkipPageItemWithSwitch.getSwitch().setChecked(isOpenSkipPage);
        openSkipPageItemWithSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.setOpenSkipPage(isChecked);
            }
        });


        //?????????????????????????????????????????????
        boolean isShowUrlRedirectTipDialog = presenter.isShowUrlRedirectTipDialog();
        QMUICommonListItemView showUrlRedirectTipDialogItemWithSwitch = qmuiGroupListView.createItemView("??????????????????????????????????????????");
        showUrlRedirectTipDialogItemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        showUrlRedirectTipDialogItemWithSwitch.getSwitch().setChecked(isShowUrlRedirectTipDialog);
        showUrlRedirectTipDialogItemWithSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.setShowUrlRedirectTipDialog(isChecked);
            }
        });

        //???????????????????????????
        boolean fixMainNavigation = presenter.isFixMainNavigation();
        QMUICommonListItemView fixMainNavigationItemWithSwitch = qmuiGroupListView.createItemView("???????????????????????????(?????????)");
        fixMainNavigationItemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        fixMainNavigationItemWithSwitch.getSwitch().setChecked(fixMainNavigation);
        fixMainNavigationItemWithSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.setFixMainNavigation(isChecked);
            }
        });


        sec.addItemView(itemWithSwitch, null);
        sec.addItemView(itemWithSwitchForbidden, this);
        sec.addItemView(openSkipPageItemWithSwitch, null);
        sec.addItemView(showUrlRedirectTipDialogItemWithSwitch, null);
        sec.addItemView(fixMainNavigationItemWithSwitch, null);
        sec.addItemView(verifyGoogleRecaptcha(), this);
        sec.addTo(qmuiGroupListView);
    }

    /**
     * ???????????????????????????
     */
    public void selectDownloadVideoDir(final QMUICommonListItemView qmuiCommonListItemView) {
        if (presenter.isHaveUnFinishDownloadVideo()) {
            showMessage("?????????????????????????????????????????????", TastyToast.INFO);
            return;
        }
        FilePicker picker = new FilePicker(this, FilePicker.DIRECTORY);
        picker.setRootPath(StorageUtils.getExternalRootPath());
        picker.setTitleText("???????????????");
        picker.setItemHeight(40);
        picker.setOnFilePickListener(new FilePicker.OnFilePickListener() {
            @Override
            public void onFilePicked(String currentPath) {
                if (presenter.getCustomDownloadVideoDirPath().equalsIgnoreCase(currentPath + "/")) {
                    showMessage("????????????????????????", TastyToast.WARNING);
                    return;
                }
                if (presenter.isHaveFinishDownloadVideoFile()) {
                    showIsMoveOldDirVideoFileToNewDirDialog(currentPath, qmuiCommonListItemView);
                } else {
                    showMessage("????????????", TastyToast.SUCCESS);
                    qmuiCommonListItemView.setDetailText(currentPath);
                    presenter.setCustomDownloadVideoDirPath(currentPath);
                }
            }
        });
        picker.show();
    }

    private QMUICommonListItemView verifyGoogleRecaptcha() {
        QMUICommonListItemView googleRecaptchaItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.google_recaptcha_verify));
        googleRecaptchaItemWithChevron.setId(R.id.setting_item_google_recaptcha_verify);
        googleRecaptchaItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);

        googleRecaptchaItemWithChevron.setDetailText("????????????Google?????????");
        googleRecaptchaItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        return googleRecaptchaItemWithChevron;
    }

    private void showIsMoveOldDirVideoFileToNewDirDialog(final String newDirPath, final QMUICommonListItemView qmuiCommonListItemView) {
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(context);
        builder.setTitle("????????????");
        builder.setMessage("????????????????????????????????????" + newDirPath + "\n?????????????????????????????????????????????????????????????????????????????????\n PS:???????????????????????????????????????????????????");
        builder.addAction("??????", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                presenter.moveOldDownloadVideoToNewDir(newDirPath, qmuiCommonListItemView);
            }
        });
        builder.addAction("?????????", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                qmuiCommonListItemView.setDetailText(newDirPath);
                presenter.setCustomDownloadVideoDirPath(newDirPath);
            }
        });
        builder.addAction("??????", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private String getAddressSettingTitle(String key) {
        switch (key) {
            case AppPreferencesHelper.KEY_SP_PORN_91_VIDEO_ADDRESS:
                return "9*porn??????????????????";
            case AppPreferencesHelper.KEY_SP_FORUM_91_PORN_ADDRESS:
                return "9*porn??????????????????";
            case AppPreferencesHelper.KEY_SP_PIG_AV_ADDRESS:
                return "P*gav????????????";
            case AppPreferencesHelper.KEY_SP_AXGLE_ADDRESS:
                return "A*gle????????????";
            case AppPreferencesHelper.KEY_SP_KE_DOU_WO_ADDRESS:
                return "KeDouWo????????????";
            default:
                return "????????????";
        }
    }

    private void showAddressSettingDialog(final QMUICommonListItemView qmuiCommonListItemView, final String key) {
        View view = getLayoutInflater().inflate(R.layout.dialog_setting_address, qmuiCommonListItemView, false);
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle(getAddressSettingTitle(key))
                .setView(view)
                .setCancelable(false)
                .show();
        AppCompatButton okAppCompatButton = view.findViewById(R.id.bt_dialog_address_setting_ok);
        AppCompatButton backAppCompatButton = view.findViewById(R.id.bt_dialog_address_setting_back);
        AppCompatButton testAppCompatButton = view.findViewById(R.id.bt_dialog_address_setting_test);
        final AppCompatAutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.atv_dialog_address_setting_address);
        autoCompleteTextView.setText(testBaseUrl);
        if (!TextUtils.isEmpty(testBaseUrl)) {
            autoCompleteTextView.setSelection(testBaseUrl.length());
        } else {
            switch (key) {
                case AppPreferencesHelper.KEY_SP_PORN_91_VIDEO_ADDRESS:
                    autoCompleteTextView.setText(presenter.getVideo9PornAddress());
                    break;
                case AppPreferencesHelper.KEY_SP_FORUM_91_PORN_ADDRESS:
                    autoCompleteTextView.setText(presenter.getForum9PornAddress());
                    break;
                case AppPreferencesHelper.KEY_SP_PIG_AV_ADDRESS:
                    autoCompleteTextView.setText(presenter.getPavAddress());
                    break;
                case AppPreferencesHelper.KEY_SP_AXGLE_ADDRESS:
                    autoCompleteTextView.setText(presenter.getAxgleAddress());
                    break;
                case AppPreferencesHelper.KEY_SP_KE_DOU_WO_ADDRESS:
                    autoCompleteTextView.setText(presenter.getKeDouWoAddress());
                    break;

                default:
            }
        }
        final String[] address = {"http://", "https://", "http://www.", "https://www."};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_auto_complete_textview, address);
        autoCompleteTextView.setAdapter(adapter);

        okAppCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = autoCompleteTextView.getText().toString().trim();
                if (TextUtils.isEmpty(address)) {
                    showMessage("????????????????????????", TastyToast.ERROR);
                    return;
                }
                //????????????????????????????????????????????????????????????????????????????????????/????????????????????????????????????
                if (!address.endsWith("/")) {
                    address += "/";
                }
                if (!checkAddress(address)) {
                    return;
                }
                testBaseUrl = address;
                alertDialog.dismiss();
                if (isTestSuccess) {
                    saveToSpAndUpdateQMUICommonListItemView(key, qmuiCommonListItemView, address);
                } else {
                    showConfirmDialog(qmuiCommonListItemView, address, key);
                }
            }
        });
        backAppCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetOrUpdateAddress(key);
                alertDialog.dismiss();
            }
        });
        testAppCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = autoCompleteTextView.getText().toString().trim();
                if (!checkAddress(address)) {
                    return;
                }
                testBaseUrl = address;
                alertDialog.dismiss();
                beginTestAddress(address, qmuiCommonListItemView, key);
            }
        });
    }

    private void beginTestAddress(String address, QMUICommonListItemView qmuiCommonListItemView, String key) {
        switch (key) {
            case AppPreferencesHelper.KEY_SP_PORN_91_VIDEO_ADDRESS:
                presenter.test9PornVideo(address, qmuiCommonListItemView, key);
                break;
            case AppPreferencesHelper.KEY_SP_FORUM_91_PORN_ADDRESS:
                presenter.test9PornForum(address, qmuiCommonListItemView, key);
                break;
            case AppPreferencesHelper.KEY_SP_PIG_AV_ADDRESS:
                presenter.testPav(address, qmuiCommonListItemView, key);
                break;
            case AppPreferencesHelper.KEY_SP_AXGLE_ADDRESS:
                presenter.testAxgle(address, qmuiCommonListItemView, key);
                break;
            default:
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param key key
     */
    private void resetOrUpdateAddress(String key) {
        switch (key) {
            case AppPreferencesHelper.KEY_SP_PORN_91_VIDEO_ADDRESS:
                // ?????? BaseUrl ?????????????????? Domain-Name header ??????????????????,???????????????????????????????????? BaseUrl ?????????
                if (!TextUtils.isEmpty(presenter.getVideo9PornAddress())) {
                    RetrofitUrlManager.getInstance().putDomain(Api.PORN9_VIDEO_DOMAIN_NAME, presenter.getVideo9PornAddress());
                }
                break;
            case AppPreferencesHelper.KEY_SP_FORUM_91_PORN_ADDRESS:
                if (!TextUtils.isEmpty(presenter.getForum9PornAddress())) {
                    RetrofitUrlManager.getInstance().putDomain(Api.PORN9_FORUM_DOMAIN_NAME, presenter.getForum9PornAddress());
                }
                break;
            case AppPreferencesHelper.KEY_SP_PIG_AV_ADDRESS:
                if (!TextUtils.isEmpty(presenter.getPavAddress())) {
                    RetrofitUrlManager.getInstance().putDomain(Api.PA_DOMAIN_NAME, presenter.getPavAddress());
                }
                break;
            case AppPreferencesHelper.KEY_SP_AXGLE_ADDRESS:
                if (!TextUtils.isEmpty(presenter.getAxgleAddress())) {
                    RetrofitUrlManager.getInstance().putDomain(Api.AXGLE_DOMAIN_NAME, presenter.getAxgleAddress());
                }
                break;
            case AppPreferencesHelper.KEY_SP_KE_DOU_WO_ADDRESS:
                if (!TextUtils.isEmpty(presenter.getKeDouWoAddress())) {
                    RetrofitUrlManager.getInstance().putDomain(Api.KE_DOU_WO_DOMAIN_NAME, presenter.getKeDouWoAddress());
                }
                break;
            default:
        }
    }

    /**
     * ?????????????????????????????????sp?????????????????????????????????
     *
     * @param key                    key
     * @param qmuiCommonListItemView qc
     * @param address                address
     */
    private void saveToSpAndUpdateQMUICommonListItemView(String key, QMUICommonListItemView qmuiCommonListItemView, String address) {
        switch (key) {
            case AppPreferencesHelper.KEY_SP_PORN_91_VIDEO_ADDRESS:
                presenter.setPorn9VideoAddress(address);
                break;
            case AppPreferencesHelper.KEY_SP_FORUM_91_PORN_ADDRESS:
                presenter.setPorn9ForumAddress(address);
                break;
            case AppPreferencesHelper.KEY_SP_PIG_AV_ADDRESS:
                presenter.setPavAddress(address);
                break;
            case AppPreferencesHelper.KEY_SP_AXGLE_ADDRESS:
                presenter.setAxgleAddress(address);
                break;
            case AppPreferencesHelper.KEY_SP_KE_DOU_WO_ADDRESS:
                presenter.setKeDouWoAddress(address);
                break;
            default:
        }
        qmuiCommonListItemView.setDetailText(address);
        showMessage("????????????", TastyToast.INFO);
        testBaseUrl = "";
    }

    private void showConfirmDialog(final QMUICommonListItemView qmuiCommonListItemView, final String address, final String key) {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle("????????????")
                .setMessage("?????????????????????????????????????????????")
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        saveToSpAndUpdateQMUICommonListItemView(key, qmuiCommonListItemView, address);
                        //??????????????????????????????
                        resetOrUpdateAddress(key);
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAddressSettingDialog(qmuiCommonListItemView, key);
                    }
                })
                .show();
    }

    private boolean checkAddress(String address) {
        HttpUrl httpUrl = HttpUrl.parse(address);
        if (httpUrl == null) {
            showMessage("?????????????????????????????????????????????(???????????????????????????/???)", TastyToast.ERROR);
            return false;
        }
        List<String> pathSegments = httpUrl.pathSegments();
        if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
            showMessage("?????????????????????????????????????????????(???????????????????????????/???)", TastyToast.ERROR);
            return false;
        }
        return true;
    }

    private void showForbiddenReleaseMemoryTipInfoDialog() {
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this);
        builder.setTitle("????????????");
        builder.setMessage("?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
        builder.addAction("?????????", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showPlaybackEngineChoiceDialog(final QMUICommonListItemView qmuiCommonListItemView) {
        final int checkedIndex = presenter.getPlaybackEngine();
        new QMUIDialog.CheckableDialogBuilder(this)
                .setTitle("??????????????????")
                .setCheckedIndex(checkedIndex)
                .addItems(PlaybackEngine.PLAY_ENGINE_ITEMS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.setPlaybackEngine(which);
                        qmuiCommonListItemView.setDetailText(PlaybackEngine.PLAY_ENGINE_ITEMS[which]);
                        showMessage("????????????", TastyToast.SUCCESS);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("????????????");
        builder.setMessage("?????????????????????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.existLogin();
                Intent intent = new Intent(SettingActivity.this, UserLoginActivity.class);
                startActivityForResultWithAnimation(intent, Constants.USER_LOGIN_REQUEST_CODE);
                finish();
            }
        });
        builder.setNegativeButton("??????", null);
        builder.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_setting_exit_account:
                showExitDialog();
                break;
            case R.id.setting_item_player_engine_choice:
                showPlaybackEngineChoiceDialog((QMUICommonListItemView) v);
                break;
            case R.id.setting_item_t6y_forum_address:
                showMessage("??????????????????????????????", TastyToast.INFO);
//                showAddressSettingDialog((QMUICommonListItemView) v, "");
                break;
            case R.id.setting_item_google_recaptcha_verify:
                String address = presenter.getVideo9PornAddress();
                if (TextUtils.isEmpty(address)) {
                    showMessage("????????????9*PORN??????", TastyToast.INFO);
                    return;
                }
                Intent intent = new Intent(this, GoogleRecaptchaVerifyActivity.class);
                startActivityWithAnimation(intent);
                break;
            default:
        }
    }


    @Override
    public void showTestingAddressDialog(boolean isTest) {
        isTestSuccess = false;
        testAlertDialog.show();
    }

    @Override
    public void testNewAddressSuccess(String message, QMUICommonListItemView qmuiCommonListItemView, String key) {
        isTestSuccess = true;
        dismissDialog();
        showMessage(message, TastyToast.SUCCESS);
        showAddressSettingDialog(qmuiCommonListItemView, key);
    }

    @Override
    public void testNewAddressFailure(String message, QMUICommonListItemView qmuiCommonListItemView, String key) {
        isTestSuccess = false;
        showMessage(message, TastyToast.ERROR);
        showAddressSettingDialog(qmuiCommonListItemView, key);
        dismissDialog();
    }

    @Override
    public void beginMoveOldDirDownloadVideoToNewDir() {
        moveOldDirDownloadVideoToNewDirDiaog.show();
    }

    @Override
    public void setNewDownloadVideoDirSuccess(String message) {
        dismissDialog();
        showMessage(message, TastyToast.SUCCESS);
    }

    @Override
    public void setNewDownloadVideoDirError(String message) {
        dismissDialog();
        showMessage(message, TastyToast.ERROR);
    }

    private void dismissDialog() {
        if (testAlertDialog.isShowing() && !isFinishing()) {
            testAlertDialog.dismiss();
        } else if (moveOldDirDownloadVideoToNewDirDiaog.isShowing() && !isFinishing()) {
            moveOldDirDownloadVideoToNewDirDiaog.dismiss();
        }
    }
}
