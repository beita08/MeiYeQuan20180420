package com.xasfemr.meiyaya.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.DynamicActivity;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.activity.MyFriendsActivity;
import com.xasfemr.meiyaya.activity.MyMessageActivity;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.view.LoadDataView;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * 2017年9月4日将动态模块命名修改为美业圈,所以模块命名为DynamicFragment
 */
public class DynamicFragment extends MVPBaseFragment implements View.OnClickListener {

    private Intent         mIntent;
    private MainActivity   mainActivity;

    @BindView(R.id.rl_dynamic_friends)
     RelativeLayout rlFriends;

    @BindView(R.id.rl_dynamic_dynamic)
     RelativeLayout rlDynamic;

    @BindView(R.id.rl_my_message)
     RelativeLayout rlMyMessage;

    @BindView(R.id.iv_msg_redDot)
     ImageView      ivMsgRedDot;

    //@Override
    //public void onCreate(@Nullable Bundle savedInstanceState) {
    //    super.onCreate(savedInstanceState);
    //    mainActivity = (MainActivity) getActivity();
    //}

    @Override
    protected int layoutId() {
        return R.layout.fragment_dynamic;
    }

    @Override
    public void initView() {
        mainActivity = (MainActivity) getActivity();
        /*View view = View.inflate(mainActivity, R.layout.fragment_dynamic, null);
        rlFriends = (RelativeLayout) view.findViewById(R.id.rl_dynamic_friends);
        rlDynamic = (RelativeLayout) view.findViewById(R.id.rl_dynamic_dynamic);
        rlMyMessage = (RelativeLayout) view.findViewById(R.id.rl_my_message);
        ivMsgRedDot = (ImageView) view.findViewById(R.id.iv_msg_redDot);*/

        rlFriends.setOnClickListener(this);
        rlDynamic.setOnClickListener(this);
        rlMyMessage.setOnClickListener(this);

        //return view;
    }

    @Override
    protected void getLoadView(LoadDataView mLoadView) {

    }

    @Override
    public void initData() {

    }

    @Override
    protected void initPresenter() {

    }

    @Override
    public void onStart() {
        super.onStart();
        updateRedDot();
    }

    @Override
    public void onClick(View v) {
        if (mIntent == null) {
            mIntent = new Intent();
        }

        boolean isLoginState = SPUtils.getboolean(mainActivity, GlobalConstants.isLoginState, false);
        String mUserId = SPUtils.getString(mainActivity, GlobalConstants.userID, "");

        switch (v.getId()) {
            case R.id.rl_dynamic_friends: //好友

                if (isLoginState) {
                    mIntent.setClass(mainActivity, MyFriendsActivity.class);
                    mIntent.putExtra("LOOK_USER_ID", mUserId);
                } else {
                    mIntent.setClass(mainActivity, LoginActivity.class);
                }
                mainActivity.startActivity(mIntent);
                break;

            case R.id.rl_dynamic_dynamic: //动态
                mIntent.setClass(mainActivity, DynamicActivity.class);
                mainActivity.startActivity(mIntent);

                break;

            case R.id.rl_my_message:      //我的消息通知

                if (isLoginState) {
                    mIntent.setClass(mainActivity, MyMessageActivity.class);
                } else {
                    mIntent.setClass(mainActivity, LoginActivity.class);
                }
                mainActivity.startActivity(mIntent);
                break;

            default:
                break;
        }
    }

    public void updateRedDot() {
        String unReadCount = mainActivity.getUnReadCount();
        if (TextUtils.isEmpty(unReadCount) || TextUtils.equals(unReadCount, "null")) {
            ivMsgRedDot.setVisibility(View.INVISIBLE);
        } else {
            ivMsgRedDot.setVisibility(View.VISIBLE);
        }
    }

    //MainActivity中调用此方法
    @Override
    public void updateRedDotByActivity(String unReadCount) {
        super.updateRedDotByActivity(unReadCount);
        if (TextUtils.isEmpty(unReadCount) || TextUtils.equals(unReadCount, "null")) {
            ivMsgRedDot.setVisibility(View.INVISIBLE);
        } else {
            ivMsgRedDot.setVisibility(View.VISIBLE);
        }
    }
}