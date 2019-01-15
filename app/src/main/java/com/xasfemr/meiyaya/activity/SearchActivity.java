package com.xasfemr.meiyaya.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Set;

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SearchActivity";

    private static final String SEARCH_HISTORY = "SEARCH_HISTORY";

    String[]          searchHotData = new String[]{"美容师招聘", "求职高级护理", "美导招聘", "美容养生", "激光仪转让", "美容治疗师", "美容院经营"};
    ArrayList<String> searchHisList = new ArrayList<>();

    private ImageView     ivTopBack;
    private TextView      tvGotoSearch;
    private EditText      etSearchContent;
    private ImageView     ivCloseSearchContent;
    private ImageView     ivDeleteSearchHistory;
    private TagFlowLayout flSearchHis;
    private TextView      tvNoSearchHistory;
    private TagFlowLayout flSearchHot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ivTopBack = (ImageView) findViewById(R.id.iv_top_back);
        tvGotoSearch = (TextView) findViewById(R.id.tv_goto_search);
        etSearchContent = (EditText) findViewById(R.id.et_search_content);
        ivCloseSearchContent = (ImageView) findViewById(R.id.iv_close_search_content);

        ivDeleteSearchHistory = (ImageView) findViewById(R.id.iv_delete_search_history);
        flSearchHis = (TagFlowLayout) findViewById(R.id.fl_search_history);
        tvNoSearchHistory = (TextView) findViewById(R.id.tv_no_search_history);
        flSearchHot = (TagFlowLayout) findViewById(R.id.fl_search_hot);

        ivTopBack.setOnClickListener(this);
        tvGotoSearch.setOnClickListener(this);
        ivCloseSearchContent.setOnClickListener(this);
        ivDeleteSearchHistory.setOnClickListener(this);

        etSearchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchContent = s.toString().trim();
                if (TextUtils.isEmpty(searchContent)) {
                    ivCloseSearchContent.setVisibility(View.GONE);
                } else {
                    ivCloseSearchContent.setVisibility(View.VISIBLE);
                }
            }
        });

        initSearchHotData();

        getSearchHisDataFromSP();
        //searchHisList有bug,总是包含一个 "" 元素,在此排除
        if (searchHisList.contains("")) {
            searchHisList.remove("");
        }

        for (int i = 0; i < searchHisList.size(); i++) {
            System.out.println("searchHisList.size()" + searchHisList.size());
            System.out.println(searchHisList.get(i));
        }

        if (searchHisList.isEmpty()) {
            flSearchHis.setVisibility(View.GONE);
            tvNoSearchHistory.setVisibility(View.VISIBLE);
            ivDeleteSearchHistory.setVisibility(View.GONE);
        } else {
            flSearchHis.setVisibility(View.VISIBLE);
            tvNoSearchHistory.setVisibility(View.GONE);
            ivDeleteSearchHistory.setVisibility(View.VISIBLE);
            initSearchHisData();
        }
    }

    private void initSearchHisData() {

        flSearchHis.setAdapter(new TagAdapter<String>(searchHisList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                View view = View.inflate(SearchActivity.this, R.layout.item_search_tag, null);
                TextView tvSearchTag = (TextView) view.findViewById(R.id.tv_search_tag);
                tvSearchTag.setText(s);
                tvSearchTag.setBackgroundResource(R.drawable.circle_ring_grey);
                return view;
            }
        });


    }

    private void initSearchHotData() {

        flSearchHot.setAdapter(new TagAdapter<String>(searchHotData) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                View view = View.inflate(SearchActivity.this, R.layout.item_search_tag, null);
                TextView tvSearchTag = (TextView) view.findViewById(R.id.tv_search_tag);
                tvSearchTag.setText(s);
                tvSearchTag.setBackgroundResource(R.drawable.circle_ring_red);
                return view;
            }
        });

        flSearchHot.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                //Toast.makeText(SearchActivity.this, searchHotData[position], Toast.LENGTH_SHORT).show();
                System.out.println("position : " + searchHotData[position]);
                return true;
            }
        });

        flSearchHot.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                System.out.println("choosechoosechoosechoose:" + selectPosSet.toString());
            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_top_back:
                finish();
                break;
            case R.id.tv_goto_search:

                ToastUtil.showShort(this,"搜索功能暂未开放");

//                String searchContent = etSearchContent.getText().toString().trim();
//
//                if (searchContent.contains("\n")) {
//                    searchContent = searchContent.replace("\n", "");
//                }
//
//                if (TextUtils.isEmpty(searchContent)) {
//                    Toast.makeText(SearchActivity.this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    String oldHistory = SPUtils.getString(SearchActivity.this, SEARCH_HISTORY, "");
//                    // 把搜索内容放到sp中,作为搜索历史
//                    SPUtils.putString(SearchActivity.this, SEARCH_HISTORY, searchContent + " " + oldHistory);
//
//                    Log.i(TAG, "搜索历史保存" + searchContent);
//                    Toast.makeText(this, "搜索" + searchContent, Toast.LENGTH_SHORT).show();

                    /*Intent searchIntent = new Intent(mSearchActivity, SearchResultActivity.class);
                    searchIntent.putExtra(Constants.SEARCH_CONTENT, searchContent);
                    startActivity(searchIntent);*/
//                }
                break;
            case R.id.iv_close_search_content:
                etSearchContent.setText("");
                break;
            case R.id.iv_delete_search_history:
                SPUtils.remove(SearchActivity.this, SEARCH_HISTORY);
                searchHisList.clear();
                flSearchHis.setVisibility(View.GONE);
                tvNoSearchHistory.setVisibility(View.VISIBLE);
                ivDeleteSearchHistory.setVisibility(View.GONE);
                break;

            default:
                break;
        }

    }

    /**
     * 从sp文件获取搜索历史
     * 搜索历史原本应该放在本地数据库中更好,但是SP中更轻量
     */
    private void getSearchHisDataFromSP() {
        searchHisList.clear();

        String mSH = SPUtils.getString(SearchActivity.this, SEARCH_HISTORY, "");
        Log.i(TAG, "mSH = " + mSH);
        String history;
        // 然后将搜索历史mSH切割存入searchHisList集合其中
        while (!(mSH == "") && !(mSH == " ") && !(mSH == null)) {

            if (mSH.contains(" ")) {
                history = mSH.substring(0, mSH.indexOf(" ") + 1).trim();
            } else {
                history = mSH;
            }

            if (!searchHisList.contains(history)) {
                searchHisList.add(history);
            }

            if (mSH.contains(" ")) {
                mSH = mSH.substring(mSH.indexOf(" ") + 1);
            } else {
                mSH = " ";
            }

            Log.i(TAG, "mSH = " + mSH);
        }
        Log.i(TAG, SEARCH_HISTORY);
    }
}
